/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.user

import static org.springframework.http.HttpStatus.*
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured(["ROLE_USER_MANAGER"])
class UserManagementController {

    // Arranged by name
    def exportService
    def springSecurityService
    def userManagementService

    private User userInstance

    static responseFormats = ["json"]

    /**
     * 
     * @param dbType Type of database support. Must be either "Mongo" or "Mysql"
     * @return
     */
    def index(Integer max, int offset, String dbType) {
        log.info "Params recived to fetch users :" + params

        params.offset = offset ?: 0
        params.max = Math.min(max ?: 10, 100)
        params.order = params.order ?: "desc"

        Map result = userManagementService."listFor${dbType}"(params)
        if (offset == 0) {
            result["roleList"] = Role.list()
        }

        render result as JSON
    }

    def modifyRoles() {
        Map requestData = request.JSON
        log.info "Parameters recevied to modify roles: $requestData"

        Set failedUsersForRoleModification = []
        List userIds = requestData.userIds
        List roleInstanceList = Role.getAll(userManagementService.getAppropiateIdList(requestData.roleIds))

        requestData.userIds.each { userId ->
            User userInstance = User.get(userId)

            if (requestData.roleActionType == "refresh") {
                UserRole.removeAll(userInstance)
            }
            roleInstanceList.each { roleInstance ->
                UserRole userRoleInstance = UserRole.create(userInstance, roleInstance, true)
                if (!userRoleInstance) {
                    failedUsersForRoleModification << userInstance.email
                }
            }
        }

        Map result = [success: true, message: "Roles updated succesfully."]

        if (failedUsersForRoleModification) {
            result["success"] = true
            result["message"] = "Unable to grant role for users with email(s) ${failedUsersForRoleModification.join(', ')}."
        }

        respond(result)
    }

    def makeUserActiveInactive() {
        Map requestData = request.JSON
        String typeText = requestData.type ? 'active': 'inactive'

        log.info "Users ID recived to $typeText User: $requestData.selectedIds"

        boolean useMongo = grailsApplication.config.cc.plugins.crm.persistence.provider == "mongodb"

        List selectedUserIds = userManagementService.getAppropiateIdList(requestData.selectedIds)

        if (!selectedUserIds) {
            respond([success: false, message: "Please select atleast one user."])
            return
        }

        try {
            if (useMongo) {
                // Returns http://api.mongodb.org/java/current/com/mongodb/WriteResult.html
                def writeResult = User.collection.update([_id: [$in: requestData.selectedIds]], [$set:
                    [enabled: requestData.type]], false, true)

                int updatedFields = writeResult.getN()
                respond ([message: "Total $updatedFields user's account set to $typeText successfully.", success: true])
            } else {
                User.executeUpdate("UPDATE User SET enabled = :actionType WHERE id IN :userIds", [
                    actionType: requestData.type, userIds: requestData.selectedIds])

                respond ([message: "User's account set to $typeText successfully."])
            }
        } catch (Exception e) {
            log.error "Error enable/disable user.", e
            respond ([message: "Unable to enable/disable the user. Please try again.", success: false])
        }

    }

    def export(boolean selectAll) {
        Map parameters, labels = [:]
        List fields = [], columnWidthList = []
        List<User> userList = userManagementService.getSelectedItemList(selectAll, params.selectedIds, params)

        fields << "id"; labels."id" = "User Id"; columnWidthList << 0.1
        fields << "email"; labels."email" = "Email"; columnWidthList << 0.3
        fields << "firstName"; labels."firstName" = "First Name"; columnWidthList << 0.2
        fields << "lastName"; labels."lastName" = "Last Name"; columnWidthList << 0.2
        fields << "gender"; labels."gender" = "Gender"; columnWidthList << 0.1
        fields << "birthdate"; labels."birthdate" = "Birthdate"; columnWidthList << 0.2
        fields << "dateCreated"; labels."dateCreated" = "Date Joined"; columnWidthList << 0.2
        fields << "enabled"; labels."enabled" = "Active"; columnWidthList << 0.1
        fields << "accountLocked"; labels."accountLocked" = "Locked"; columnWidthList << 0.1

        parameters = ["column.widths": columnWidthList]

        response.contentType = "application/vnd.ms-excel"
        response.setHeader("Content-disposition", "attachment; filename=user-report.csv");

        exportService.export("csv", response.outputStream, userList, fields, labels, [:], parameters)
    }

    def updateEmail() {
        params.putAll(request.JSON as Map)
        log.debug "Params reveived to update email $params"

        if (!params.id || !params.newEmail || !params.confirmNewEmail) {
            response.setStatus(NOT_ACCEPTABLE)
            respond ([message: "Please select a user and enter new & confirmation email."])
            return
        }

        params.email = params.email.toLowerCase()
        params.confirmNewEmail = params.confirmNewEmail.toLowerCase()

        if (params.newEmail != params.confirmNewEmail) {
            response.setStatus(NOT_ACCEPTABLE)
            respond ([message: "Email dose not match the Confirm Email."])
            return
        }

        if (User.countByEmail(params.newEmail)) {
            response.setStatus(NOT_ACCEPTABLE)
            respond ([message: "User already exist with Email: $params.newEmail"])
            return
        }

        User userInstance = User.get(params.id)
        if (!userInstance) {
            response.setStatus(NOT_ACCEPTABLE)
            respond ([message: "User not found with id: $params.id."])
            return
        }

        userInstance.email = params.newEmail
        userInstance.save()
        if (userInstance.hasErrors()) {
            response.setStatus(NOT_ACCEPTABLE)

            log.warn "Error saving $userInstance $userInstance.errors"

            respond ([message: "Unable to update user's email.", error: userInstance.errors])
            return
        }

        respond ([message: "Email updated Successfully."])
    }
}