package com.cc.user

import grails.converters.JSON
import grails.plugins.springsecurity.Secured

@Secured(["ROLE_ADMIN"])
class UserManagementController {

    // Arranged by name
    def exportService
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

        Map labels = [:]
        List parameters, fields = [], columnWidthList = []
        List selectedUser = params.selectedUser.tokenize(",")*.trim()*.toLong()

        fields << "id"; labels."id" = "User Id"; columnWidthList << 0.1
        fields << "email"; labels."email" = "Email"; columnWidthList << 0.3
        fields << "firstName"; labels."firstName" = "First Name"; columnWidthList << 0.2
        fields << "lastName"; labels."lastName" = "Last Name"; columnWidthList << 0.2
        fields << "gender"; labels."gender" = "Gender"; columnWidthList << 0.1
        fields << "birthdate"; labels."birthdate" = "Birthdate"; columnWidthList << 0.2
        fields << "dateCreated"; labels."dateCreated" = "Date Joined"; columnWidthList << 0.2
        fields << "enabled"; labels."enabled" = "Active"; columnWidthList << 0.1
        fields << "accountLocked"; labels."accountLocked" = "Locked"; columnWidthList << 0.1

        response.contentType = "application/vnd.ms-excel"
        response.setHeader("Content-disposition", "attachment; filename=user-report.csv");

        exportService.export("excel", response.outputStream, User.getAll(selectedUser), fields, labels, [:], parameters)
    }
}
