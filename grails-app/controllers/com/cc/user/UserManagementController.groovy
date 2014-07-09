/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.user

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured

@Secured(["ROLE_CONTENT_MANAGER"])
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
        log.info "Parameters received to change roles of User: $params.userIds with roleIds: $params.roleIds"

        List userIds = params.list("userIds")
        List roles = params.list("roleIds")

        User currentUserInstance = springSecurityService.currentUser
        if (!SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN')) {
            Role adminRole = Role.findByAuthority("ROLE_ADMIN")
            User adminUserInstance = User.findByRoleIds(adminRole.id)

            userIds = userIds.minus(adminUserInstance.id.toString())
            roles = roles.minus(adminRole.id.toString())

            log.info "Removed Admin User and admin autority Parameters from received list of" +
                    " user: $userIds and roleIds: $roles"
        }
        List roleInstanceList = Role.getAll(roles*.toLong())

        userIds.each { userId ->
            User userInstance = User.get(userId as long)

            if(roleActionType == "refresh") {
                UserRole.removeAll(userInstance)
            }
            roleInstanceList.each { roleInstance ->
                UserRole.create(userInstance, roleInstance, true)
            }
        }
        render true
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

    def exportUserReport() {
        log.info "User List for download Emails: $params.selectedUser."

        Map parameters, labels = [:]
        List fields = [], columnWidthList = []

        List<User> userList = []
        if (params.boolean('selectAllPageUsers')) {
            log.info "Params received to download all User List report."
            userList = User.list()
        } else {
            log.info "User List for download report: $params.selectedUser."
            List selectedUser = params.selectedUser.tokenize(",")*.trim()*.toLong()
            userList = User.getAll(selectedUser)
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
        response.setHeader("Content-disposition", "attachment; filename=user-report.xls");

        exportService.export("excel", response.outputStream, userList, fields, labels, [:], parameters)
    }
}
