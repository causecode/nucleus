/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.user

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured(["ROLE_CONTENT_MANAGER"])
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

        Map result = userManagementService."listFor${dbType}"(params)
        if (offset == 0) {
            result["roleList"] = Role.list()
        }

        render result as JSON
    }

    def modifyRoles() {
        Map requestData = request.JSON
        log.info "Parameters recevied to modify roles: $requestData"

        List userIds = requestData.userIds
        List roleInstanceList = Role.getAll(userManagementService.getAppropiateIdList(requestData.roleIds))

        requestData.userIds.each { userId ->
            User userInstance = User.get(userId)

            if (requestData.roleActionType == "refresh") {
                UserRole.removeAll(userInstance)
            }
            roleInstanceList.each { roleInstance ->
                UserRole.create(userInstance, roleInstance, true)
            }
        }
        render ([success: true] as JSON)
    }

    def makeUserActiveInactive() {
        Map requestData = request.JSON
        String typeText = requestData.type ? 'active': 'inactive'

        log.info "Users ID recived to $typeText User: $requestData.selectedIds"

        requestData.selectedIds.each { userId ->
            User userInstance = User.get(userId)

            if (userInstance) {
                userInstance.enabled = requestData.type
                userInstance.save(flush: true)
            } else {
                log.warn "User not found with id: $userId"
            }
        }

        String message = "User's account set to $typeText successfully."
        respond ([message: message])
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
}
