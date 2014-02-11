package com.cc.user

import grails.transaction.Transactional

@Transactional
class UserManagementService {

    List fetchListForMongo(Map params, boolean paginate) {
        List roleFilterList = params.roleFilter as List

        List result = User.withCriteria {
            if(!paginate) {
                projections {
                    countDistinct "email"
                }
            }
            if(params.letter) {
                ilike("firstName", "${params.letter}%")
            }
            if(params.query) {
                or {
                    ["firstName", "lastName", "username", "email"].each { userField ->
                        ilike(userField, "%${params.query}%")
                    }
                }
            }
            if(params.roleFilter) {
                if(params.roleType == "Any Granted") {
                    or {
                        roleFilterList*.toLong().each { roleId ->
                            between("roleIds", roleId, roleId)  // Using between as to match item in list.
                        }
                    }
                } else if(params.roleType == "All Granted") {
                    and {
                        roleFilterList*.toLong().each { roleId ->
                            between("roleIds", roleId, roleId)
                        }
                    }
                } else {
                    and {
                        eq("roleIds", roleFilterList*.toLong().sort())
                    }
                }
            }
            if(paginate) {
                firstResult(params.offset.toInteger())
                maxResults(params.max.toInteger())
                order(params.sort, params.order)
            }
        }
        result
    }

    Map listForMongo(Map params) {
        [userInstanceList: fetchListForMongo(params, true), userInstanceTotal: fetchListForMongo(params, false)[0]]
    }

    Map listForMysql(Map params) {
        String roleType = params.roleType

        Long userInstanceTotal = 0
        List<User> userInstanceList = []

        Map queryStringParams = [:]
        StringBuilder query = new StringBuilder("select distinct ur1.user from UserRole ur1")

        if(params.roleFilter) {
            List roleFilterList = params.roleFilter as List

            if(roleType == "Any Granted") {
                queryStringParams.roles = roleFilterList
                query.append(" where ur1.role.id in (:roles)")
            } else if(roleType == "All Granted") {
                query.append(" where")
                makeQueryToCheckEachRole(query, roleFilterList)
            } else {
                query.append(" where")
                makeQueryToCheckEachRole(query, roleFilterList)
                query.append(""" and exists ( select ur_count.user from UserRole ur_count where
                    ur1.user.id = ur_count.user.id group by ur_count.user having count(ur_count.role) = ${roleFilterList.size()})""")
            }
        }

        if(params.letter) {
            if(query.indexOf("where") == -1) query.append(" where")
            query.append(""" lower(ur1.user.firstName) like '${params.letter.toLowerCase()}%' """)
        }

        if(params.query && !params.letter) {
            if(query.indexOf("where") == -1) query.append(" where")
            query.append(""" lower(ur1.user.firstName) like '%${params.query.toLowerCase()}%' """)
            query.append(""" or lower(ur1.user.lastName) like '${params.query.toLowerCase()}%' """)
            query.append(""" or lower(ur1.user.email) like '${params.query.toLowerCase()}%' """)
            query.append(""" or lower(ur1.user.username) like '${params.query.toLowerCase()}%' """)
        }
        query.append(" order by ur1.user.${params.sort} ${params.order}")

        userInstanceList = UserRole.executeQuery(query.toString(), queryStringParams, [max: params.max, offset: params.offset])

        userInstanceTotal = UserRole.executeQuery(query.toString(), queryStringParams).size()

        [userInstanceList: userInstanceList, userInstanceTotal: userInstanceTotal]
    }

    void makeQueryToCheckEachRole(StringBuilder query, roleFilterList) {
        roleFilterList.eachWithIndex { role, index ->
            String alias = "ur${index+2}"
            query.append(" exists ( select ${alias}.user from UserRole $alias where ${alias}.user.id = ur1.user.id and ${alias}.role.id = $role )")
            if(index < roleFilterList.size() - 1) {
                query.append(" and")
            }
        }
    }
}