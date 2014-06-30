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

@Secured(["ROLE_ADMIN"])
class UserManagementController {

    // Arranged by name
    def exportService
    def springSecurityService
    def userManagementService

    private User userInstance

    def index() {
        redirect(action: "list", params: params)
    }

    def roleList() {
        render ([roleList: Role.list()] as JSON)
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

        render result as JSON
    }

    def modifyRoles() {
        Map requestData = request.JSON
        log.info "Parameters recevied to modify roles: $requestData"

        List userIds = requestData.userIds
        List roles = requestData.roleIds
        List roleInstanceList = Role.getAll(roles*.toLong())

        requestData.userIds.each { userId ->
            User userInstance = User.get(userId)

            if(requestData.roleActionType == "refresh") {
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
        String typeText = requestData.type ? 'active': 'in-active'
        List selectedUser = requestData.selectedUser.tokenize(',')*.toLong()

        log.info "Users ID recived to $typeText User : $requestData.selectedUser "

        selectedUser?.each {
            userInstance = User.findByIdAndEnabled(it, !requestData.type)

            if(userInstance) {
                userInstance.enabled = requestData.type
                userInstance.save(flush: true)
            }
        }
        String message = "User's account set to $typeText successfully."
        render ([message: message] as JSON)
    }

    def export(boolean selectAll) {
        List selectedIds = params.selectedIds.tokenize(",")*.trim()*.toLong()
        Map parameters, labels = [:]
        List fields = [], columnWidthList = []
        List<User> userList = []

        if (selectAll) {
            log.info "Downloading all User List report."
            userList = User.list()
        } else {
            log.info "User List for download report: $selectedIds."
            userList = User.getAll(selectedIds)
        }

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
