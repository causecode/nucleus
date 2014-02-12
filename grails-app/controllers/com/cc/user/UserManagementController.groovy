package com.cc.user

import grails.converters.JSON
import liquibase.util.csv.CSVWriter
import grails.plugins.springsecurity.Secured

@Secured(["ROLE_ADMIN"])
class UserManagementController {

    def springSecurityService
    def userManagementService

    private User userInstance

    def index() {}

    def roleList() {
        render Role.list() as JSON
    }

    /**
     * 
     * @param dbType Type of database support. Must be either "Mongo" or "Mysql"
     * @return
     */
    def list(Integer max, String dbType) {
        log.info "Params recived to fetch users :" + params

        params.offset = params.offset ?: 0
        params.sort = params.sort ?: "id"
        params.max = Math.min(max ?: 10, 100)
        params.order = params.order ?: "asc"

        Map result = userManagementService."listFor${dbType}"(params)

        result.roleList = Role.list([sort: 'authority'])

        render result as JSON
    }

    def modifyRoles(String roleActionType) {
        List userIds = params.list("userIds")
        List roles = params.list("roleIds")
        List roleInstanceList = Role.getAll(roles*.toLong())

        userIds.each { userId ->
            User userInstance = User.get(userId)

            if(roleActionType == "refresh") {
                UserRole.removeAll(userInstance)
            }
            roleInstanceList.each { roleInstance ->
                UserRole.create(userInstance, roleInstance, true)
            }
        }
        render true
    }

    def sendBulkEmail() {
        List invalidEmail = []
        String result
        List<String> selectedEmail = Arrays.asList(params.selectedEmail.split("\\s*,\\s*"));

        if(selectedEmail && params.subject && params.body) {

            selectedEmail.each {
                String emailAddress = it?.trim()
                Map emailLayoutArgs = [title: "", body: params.body]
                try {
                    sendMail {
                        to emailAddress
                        subject params.subject;
                        html g.render(template: "/userManagement/templates/email", model: [email: emailLayoutArgs], plugin:"nucleus");
                    }
                } catch(Exception e) {
                    log.error "Exception sending mail to ${emailAddress}- " + e?.dump()
                    invalidEmail.add(emailAddress)
                }
            }
            result = "Message successfully sent."
            if(invalidEmail)
                result += "<br>We're sorry, Email could not be delivered to: " + invalidEmail.join(", ")
            render ([message: result] as JSON)
            return
        }
        result = "Missing something: Email(s)/subject/body"
        render ([message: result] as JSON)
    }

    def makeUserActiveInactive() {
        String typeText = params.boolean('type') ? 'active': 'in-active'

        log.info "Users ID recived to $typeText active User : $params.selectedUser $params"

        params.list('selectedUser')?.each {
            userInstance = User.findByIdAndEnabled(it, !params.boolean("type"))

            if(userInstance) {
                userInstance.enabled = params.boolean("type")
                userInstance.save(flush: true)
            }
        }
        String message = "User's account set to $typeText successfully."
        render ([message: message] as JSON)
    }

    def downloadEmails() {
        log.info "User List for download Emails: $params.selectedUser."
        List selectedUser = params.selectedUser.tokenize(',')
        response.setHeader("Content-disposition", "attachment; filename=user-report.csv");
        def out = response.outputStream
        out.withWriter { writer ->
            String[] properties = new String[5]
            properties = ['Id', 'Full Name', 'Email', 'Username', 'Active']
            CSVWriter csvWriter = new CSVWriter(writer)
            csvWriter.writeNext(properties)
            selectedUser?.each {
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
    }
}
