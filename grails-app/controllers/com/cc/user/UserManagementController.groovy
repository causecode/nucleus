package com.cc.user

import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import liquibase.util.csv.CSVWriter

class UserManagementController {

    def springSecurityService

    private User userInstance

    def index() {}

    def list(Integer max, String roleType) {
        println "Params recived to fetch users :"+params
        params.offset = params.offset ?: 0
        params.sort = params.sort ?: "id"
        params.max = Math.min(max ?: 10, 100)
        params.order = params.order ?: "asc"

        if(params.clear)
            params.remove(params.clear)

        Long userInstanceTotal = 0
        List<User> userInstanceList = []

        Map queryStringParams = [:]
        StringBuilder query = new StringBuilder("select distinct ur1.user from UserRole ur1")

        params.remove("selectedUser"); params.remove("_selectedUser")
        params.remove("check-uncheck"); params.remove("_check-uncheck")

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
        if(params.letter && !params.roleFilter) {
            query.append(" where")
            query.append(""" lower(ur1.user.firstName) like '${params.letter.toLowerCase()}%' """)
        }
        if(params.query && !params.letter && !params.roleFilter) {
            query.append(" where")
            query.append(""" lower(ur1.user.firstName) like '%${params.query.toLowerCase()}%' """)
            query.append(""" or lower(ur1.user.lastName) like '${params.query.toLowerCase()}%' """)
            query.append(""" or lower(ur1.user.email) like '${params.query.toLowerCase()}%' """)
            query.append(""" or lower(ur1.user.username) like '${params.query.toLowerCase()}%' """)
        }
        String countQuery = query.toString()
        query.append(" order by ur1.user.${params.sort} ${params.order}")

        userInstanceList = UserRole.executeQuery(query.toString(), queryStringParams, [max: params.max, offset: params.offset])

        userInstanceTotal = UserRole.executeQuery(countQuery, queryStringParams).size()

        render ([userInstanceList: userInstanceList, userInstanceTotal: userInstanceTotal, roleList: Role.list([sort: 'authority']),
            currentUserInstance: springSecurityService.currentUser]  as JSON)
    }

    private void makeQueryToCheckEachRole(StringBuilder query, roleFilterList) {
        roleFilterList.eachWithIndex { role, index ->
            String alias = "ur${index+2}"
            query.append(" exists ( select ${alias}.user from UserRole $alias where ${alias}.user.id = ur1.user.id and ${alias}.role.id = $role )")
            if(index < roleFilterList.size() - 1) {
                query.append(" and")
            }
        }
    }

    def modifyRoles(String roleActionType) {
        List userIds = params.list("userIds")
        List roles = params.list("roleIds")
        List roleInstanceList = Role.getAll(roles)

        userIds.each { userId ->
            User userInstance = User.get(userId)

            if(roleActionType == "refresh") {
                UserRole.findAllByUser(userInstance)*.delete(flush: true)
            }
            roleInstanceList.each { roleInstance ->
                UserRole.findOrSaveByUserAndRole(userInstance, roleInstance)
            }
        }
        render true
    }

    private void saveSelectedIds() {
        List<Long> selectedUser = params.list('selectedUser')
        if(!selectedUser) return;

        if(session.selectedUser) {
            List<Long> tempList = session.selectedUser.toList()
            selectedUser.each {
                if(!tempList.contains(it)) {
                    tempList.add(it)
                }
            }
            session.selectedUser = tempList
        } else {
            session.selectedUser = []
            session.selectedUser = selectedUser
        }
        log.debug "Selected user at user management- " + session.selectedUser
    }

    def clearSelection() {
        if(params.id && session.selectedUser) {
            session.selectedUser.remove(params.id)
            log.debug "Selected user at user management- " + session.selectedUser
        } else {
            session.selectedUser = null
            render "Selection cleared."
        }
    }

    def fetchEmails() {
        saveSelectedIds()
        List emailList = User.getAll(session.selectedUser)*.email

        String emails = emailList.unique().join(', ')
        if(!emails) response.status = 500;
        render emails
    }

    @Secured(["ROLE_USER"])
    def sendBulkEmail() {
        List invalidEmail = []
        List selectedEmail = params.selectedEmail?.tokenize(',')
        if(selectedEmail && params.subject && params.body) {
            selectedEmail.each {
                String emailAddress = it?.trim()
                Map emailLayoutArgs = [title: "", body: params.body]
                try {
                    asyncMailService.sendMail {
                        to emailAddress
                        subject params.subject;
                        html g.render(template: "/layouts/email", model: [email: emailLayoutArgs]);
                    }
                } catch(Exception e) {
                    log.error "Exception sending mail to ${emailAddress}- " + e?.dump()
                    invalidEmail.add(emailAddress)
                }
            }
            String result = "Message successfully sent."
            if(invalidEmail)
                result += "<br>We're sorry, Email could not be delivered to: " + invalidEmail.join(", ")
            render result
            return
        }
        response.status = 500
        render "Missing something: Email(s)/subject/body"
    }

    def makeUserActiveInactive(Boolean type) {
        saveSelectedIds()
        session.selectedUser?.each {
            try {
                userInstance = User.findByIdAndEnabled(it, !type)
            } catch(Exception e){userInstance = null }

            if(userInstance) {
                userInstance.enabled = type
                userInstance.save(flush: true)
                /* try {
                 if(type) userInstance?.index()
                 else userInstance?.deleteSolr()
                 } catch(SolrServerException e) {
                 log.error "Error indexing/deleting for user $userInstance : $e.message"
                 }*/
            }
        }

        render "User's account set to ${type ? '' : 'in-'}active successfully."
    }

    /*def downloadEmails() { return
        //saveSelectedIds()
        response.setHeader("Content-disposition", "attachment; filename=user-report.csv");
        def out = response.outputStream
        out.withWriter { writer ->
            String[] properties = new String[5]
            properties = ['Id', 'Full Name', 'Email', 'Username', 'Active']
            def csvWriter = new CSVWriter(writer)
            csvWriter.writeNext(properties)
            session.selectedUser?.each {
                try {
                    userInstance = User.get(it)
                } catch(Exception e) {userInstance = null }
                if(userInstance) {
                    properties[0] = userInstance?.id
                    properties[1] = userInstance?.fullName
                    properties[2] = userInstance?.email
                    properties[3] = userInstance?.username
                    properties[4] = ""+userInstance?.enabled
                    csvWriter.writeNext(properties)
                }
            }
            csvWriter.flush()
        }
    }*/
}
