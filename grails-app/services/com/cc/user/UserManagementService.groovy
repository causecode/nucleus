package com.cc.user

import grails.transaction.Transactional

/**
 * UserManagementService provides database specific methods to fetch list of users with filters and pagination applied.
 * @author Shashank Agrawal
 *
 */
@Transactional
class UserManagementService {

    /**
     * Used to fetch list of user for Mongo database with filters and pagination applied. 
     * @param params List of parameters contains pagination, filter parameters.
     * @param paginate Boolean value to specify whether to use pagination parameters or not.
     * @return Filtered list of user's with distinct email.
     */
    List fetchListForMongo(Map params) {
        List roleFilterList = []

        if (params.roleFilter instanceof String) {
            roleFilterList = params.roleFilter.tokenize(",")
        } else {
            roleFilterList = params.roleFilter as List
        }

        List result = User.createCriteria().list(params) {
            if (params.letter) {
                ilike("firstName", "${params.letter}%")
            }
            if (params.query) {
                or {
                    ["firstName", "lastName", "username", "email"].each { userField ->
                        ilike(userField, "%${params.query}%")
                    }
                }
            }
            if (params.roleFilter) {
                if (params.roleType == "Any Granted") {
                    or {
                        getAppropiateIdList(roleFilterList).each { roleId ->
                            'in'("roleIds", [roleId])
                        }
                    }
                } else if (params.roleType == "All Granted") {
                    and {
                        getAppropiateIdList(roleFilterList).each { roleId ->
                            'in'("roleIds", [roleId])
                        }
                    }
                } else {
                    and {
                        eq("roleIds", getAppropiateIdList(roleFilterList).sort())
                    }
                }
            }
        }
        result
    }

    List getAppropiateIdList(List ids) {
        if (!ids) {
            return []
        }

        // Check type of id, either Long or mongo's ObjectId
        if (ids[0].isNumber()) {
            // If domain id is of type Long
            ids = ids*.toLong()
        } else {
            // If domain id in mongo's ObjectId. Dynamic loading to avoid import error
            Class ObjectIdClazz = Class.forName("org.bson.types.ObjectId")
            ids = ids.collect { ObjectIdClazz.newInstance(it) }
        }

        return ids
    }

    Map getList(params) {
        if (params.dbType == "Mongo") {
            return listForMongo(params)
        }

        return listForMysql(params)
    }

    /**
     * Used to fetch users list for Mongo Database with total count.
     * @param params List of filter parameters.
     * @return Map containing users list with pagination applied and total count of matched users.
     */
    Map listForMongo(Map params) {
        List result = fetchListForMongo(params)
        [instanceList: result, totalCount: result.totalCount]
    }

    /**
     * Used to fetch list of user for MySql database with filters and pagination applied using HQL Query.
     * @param params List of parameters contains pagination, filter parameters.
     * @return Filtered list of user's with pagination applied.
     */
    Map listForMysql(Map params) {
        String roleType = params.roleType

        Long userInstanceTotal = 0
        List<User> userInstanceList = []

        Map queryStringParams = [:]
        StringBuilder query = new StringBuilder("select distinct ur1.user from UserRole ur1")

        if (params.roleFilter) {
            List roleFilterList = params.roleFilter as List

            if (roleType == "Any Granted") {
                queryStringParams.roles = roleFilterList
                query.append(" where ur1.role.id in (:roles)")
            } else if (roleType == "All Granted") {
                query.append(" where")
                makeQueryToCheckEachRole(query, roleFilterList)
            } else {
                query.append(" where")
                makeQueryToCheckEachRole(query, roleFilterList)
                query.append(""" and exists ( select ur_count.user from UserRole ur_count where
                    ur1.user.id = ur_count.user.id group by ur_count.user having count(ur_count.role) = ${roleFilterList.size()})""")
            }
        }

        if (params.letter) {
            if (query.indexOf("where") == -1) query.append(" where")
            query.append(""" lower(ur1.user.firstName) like '${params.letter.toLowerCase()}%' """)
        }

        if (params.query && !params.letter) {
            if (query.indexOf("where") == -1) query.append(" where")
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

    /**
     * Appends HQL query string to query parameter on basis of role filters list provided.
     * @param query HQL query string to be appended.
     * @param roleFilterList List of role filters.
     */
    void makeQueryToCheckEachRole(StringBuilder query, roleFilterList) {
        roleFilterList.eachWithIndex { role, index ->
            String alias = "ur${index+2}"
            query.append(" exists ( select ${alias}.user from UserRole $alias where ${alias}.user.id = ur1.user.id and ${alias}.role.id = $role )")
            if (index < roleFilterList.size() - 1) {
                query.append(" and")
            }
        }
    }

    List getSelectedItemList(boolean selectAll, String selectedIds, Map args) {
        if (selectAll) {
            return getList(args)["instanceList"]
        } else if (selectedIds) {
            return User.getAll(getAppropiateIdList(selectedIds.tokenize(",")))
        }

        return []
    }
}