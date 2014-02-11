package com.cc.user

import grails.transaction.Transactional

@Transactional
class UserManagementService {

    Map listForMongo(Map params) {
        List userInstanceList = User.withCriteria {
            if(params.letter) {
                ilike("firstName", "${params.letter}%")
            }
            firstResult(params.offset.toInteger())
            maxResults(params.max.toInteger())
        }

        [userInstanceList: userInstanceList]
    }

    Map listForMysql(Map params) {
        String roleType = params.roleType

        Long userInstanceTotal = 0
        List<User> userInstanceList = []

        Map queryStringParams = [:]
        StringBuilder query = new StringBuilder("select distinct ur1.user from UserRole ur1")

        if(params.roleFilter) {
            List roleFilterList = params.list("roleFilter")*.toLong()

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
