/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.user

import com.causecode.util.NucleusUtils
import grails.plugins.export.ExportService
import org.springframework.http.HttpStatus
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured

/**
 * @author Vishesh Duggar
 * @author Shashank Agrawal
 * @author Laxmi Salunkhe
 */
@Secured(['ROLE_USER_MANAGER'])
class UserManagementController {

    /**
     * Dependency Injection for the exportService.
     */
    ExportService exportService

    /**
     * Dependency Injection for the userManagementService.
     */
    UserManagementService userManagementService

    static responseFormats = ['json']
    private static final String ROLE_ADMIN = 'ROLE_ADMIN'
    private static final String SELECT_ATLEAST_ONE_USER = 'Please select at least one user.'
    private static final String ROLE_ADMIN_EXCLUDED =
            'Users with role Admin are excluded from selected list.'
    private static final String DATE_CREATED = 'dateCreated'
    /**
     * List action used to fetch Role list and User's list with filters and pagination applied.
     * @param max Integer parameter used to set number of records to be returned.
     * @param dbType Type of database support. Must be either "Mongo" or "Mysql".
     * @return Result in JSON format.
     */
    def index(Integer max, int offset, String dbType) {
        String tempDbType = dbType
        params.offset = offset ?: 0
        params.max = Math.min(max ?: 10, 100)
        params.sort = params.sort ?: DATE_CREATED
        params.order = params.order ?: 'desc'
        tempDbType = tempDbType ?: 'Mysql'
        log.info "Params received to fetch users :$params"

        Map result = userManagementService."listFor${tempDbType}"(params)
        if (offset == 0) {
            result['roleList'] = Role.list()
        }
        render result as JSON
    }

    /**
     * Modifies Roles of users with help of given roles and type.
     * @param userIds List of users ID
     * @param roleIds List of role ID
     * @param roleActionType String value which specifies two conditions.
     * 1. "refresh" - Remove existing roles and apply new roles.
     * 2. other role type - Update existing roles. i.e. append roles.
     * @return Renders boolean response True.
     */
    def modifyRoles() {
        Map requestData = request.JSON
        log.info "Parameters recevied to modify roles: $requestData"

        Set failedUsersForRoleModification = []
        List userIds = userManagementService.getAppropiateIdList(requestData.userIds)
        List roleIds = userManagementService.getAppropiateIdList(requestData.roleIds)
        roleIds = roleIds*.toLong()

        if (!SpringSecurityUtils.ifAnyGranted(ROLE_ADMIN)) {
            Role adminRole = Role.findByAuthority(ROLE_ADMIN)
            List adminUsersIds = UserRole.findAllByRole(adminRole)*.user*.id
            log.info "Removing admin user ids: $adminUsersIds."

            userIds = userIds - adminUsersIds
            log.info "Removed admin users: $userIds"
            /*
             * If a User is trying to assign ADMIN Role to any User, he should not be allowed to do so.
               Only ADMIN Users can assign ADMIN role to other Users.
            */
            roleIds -= adminRole.id
            log.info '[Not authorized] Removing Admin Role ids'
        }

        if (!userIds) {
            String message = SELECT_ATLEAST_ONE_USER
            if (!SpringSecurityUtils.ifAnyGranted(ROLE_ADMIN)) {
                message += ROLE_ADMIN_EXCLUDED
            }
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value())
            respond([success: false, message: message])
            return
        }

        if (!roleIds) {
            String message = 'No Roles selected.'
            if (!SpringSecurityUtils.ifAnyGranted(ROLE_ADMIN)) {
                message += 'Only Users with Admin role can assign Admin roles.'
            }
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value())
            respond([success: false, message: message])
            return
        }

        List roleInstanceList = Role.getAll(roleIds)

        userIds.each { userId ->
            User userInstance = User.get(userId)
            if (requestData.roleActionType == 'refresh') {
                UserRole.removeAll(userInstance)
            }
            roleInstanceList.each { roleInstance ->
                UserRole userRoleInstance = UserRole.findByUserAndRole(userInstance, roleInstance)
                // To avoid MySQLIntegrityConstraintViolationException which occurs if duplicate record is inserted
                userRoleInstance = userRoleInstance ?: UserRole.create(userInstance, roleInstance, true)
                if (!userRoleInstance || userRoleInstance.hasErrors()) {
                    failedUsersForRoleModification << userInstance.email
                }
            }
        }

        if (failedUsersForRoleModification) {
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value())
            respond([success: false, message: 'Unable to grant role for users with email(s)' +
                    "${failedUsersForRoleModification.join(', ')}."])
        }

        respond([success: true, message: 'Roles updated succesfully.'])
    }

    /**
     * Marks users ACTIVE/INACTIVE with help of given type.
     * @param selectedUser List of users ID
     * @param type String value which specifies two conditions.
     * 1. "active" - Set User field enabled to true.
     * 2. "in-active" - Set User field enabled to false.
     * @return Renders message response in JSON format.
     */
    @SuppressWarnings('CatchException')
    def makeUserActiveInactive() {
        Map requestData = request.JSON
        String typeText
        if (requestData.type) {
            typeText = requestData.type
        } else {
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value())
            respond([success: false, message: 'Type String is required'])
            return
        }

        log.info "Params received to $typeText users: $requestData"

        // TODO Change cc package name to causecode after changes in crm plugin
        boolean useMongo = grailsApplication.config.cc.plugins.crm.persistence.provider == 'mongodb'

        List selectedUserIds = userManagementService.getAppropiateIdList(requestData.selectedIds)

        if (!SpringSecurityUtils.ifAnyGranted(ROLE_ADMIN)) {
            Role adminRole = Role.findByAuthority(ROLE_ADMIN)
            List adminUsersIds = UserRole.findAllByRole(adminRole)*.user*.id
            selectedUserIds = selectedUserIds - adminUsersIds
            log.info "Removed admin users: $selectedUserIds"
        }

        if (!selectedUserIds) {
            String message = SELECT_ATLEAST_ONE_USER
            if (!SpringSecurityUtils.ifAnyGranted(ROLE_ADMIN)) {
                message += ROLE_ADMIN_EXCLUDED
            }
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value())
            respond([success: false, message: message])
            return
        }

        try {
            if (useMongo) {
                // Returns http://api.mongodb.org/java/current/com/mongodb/WriteResult.html
                def writeResult = User.collection.update([_id: [$in: selectedUserIds]],
                        [$set: [enabled: requestData.type.toBoolean()]], false, true)

                int updatedFields =  writeResult.n
                respond([message: "Total $updatedFields user's account set to $typeText successfully.", success: true])
            } else {
                selectedUserIds = selectedUserIds*.toLong()

                List<User> userList = User.withCriteria {
                    'in'('id', selectedUserIds)
                } as List
                userList.each { User userInstance ->
                    userInstance.enabled = typeText == 'active' ?: false
                    userInstance.save()
                }
                respond([message: "User's account set to $typeText successfully.", success: true])
            }
        } catch (Exception e) {
            log.error 'Error enable/disable user.', e
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value())
            respond([message: 'Unable to enable/disable the user. Please try again.', success: false])
        }
    }

    /**
     * This action provides excel report for given listed users.
     * @param selectedUser List of users ID
     */
    def export(boolean selectAll) {
        Map parameters
        Map labels = [:]
        List fields = [], columnWidthList = []
        List<User> userList = userManagementService.getSelectedItemList(selectAll, params.selectedIds, params)
        float pointOne = 0.1f, pointTwo = 0.2f, pointThree = 0.3f
        String id = 'id', email = 'email', firstName = 'firstName', lastName = 'lastName', gender = 'gender',
               birthdate = 'birthdate', dateCreated = DATE_CREATED, enabled = 'enabled', accountLocked = 'accountLocked'
        fields << id; labels.id = 'User Id'; columnWidthList << pointOne
        fields << email; labels.email = 'Email'; columnWidthList << pointThree
        fields << firstName; labels.firstName = 'First Name'; columnWidthList << pointTwo
        fields << lastName; labels.lastName = 'Last Name'; columnWidthList << pointTwo
        fields << gender; labels.gender = 'Gender'; columnWidthList << pointOne
        fields << birthdate; labels.birthdate = 'Birthdate'; columnWidthList << pointTwo
        fields << dateCreated; labels.dateCreated = 'Date Joined'; columnWidthList << pointTwo
        fields << enabled; labels.enabled = 'Active'; columnWidthList << pointOne
        fields << accountLocked; labels.accountLocked = 'Locked'; columnWidthList << pointOne

        parameters = ['column.widths': columnWidthList]

        response.contentType = 'application/vnd.ms-excel'
        response.setHeader('Content-disposition', 'attachment; filename=user-report.csv')

        exportService.export('csv', response.outputStream, userList, fields, labels, [:], parameters)
    }

    /**
     * Used to update the email of any given user.
     * @param id Identity of user to update.
     * @param newEmail New email address to update
     * @param confirmNewEmail Confirm new email
     * @return Status message with success or not.
     */
    def updateEmail() {
        params.putAll(request.JSON as Map)
        log.debug "Params received to update email: $params"

        if (!params.id || !params.newEmail || !params.confirmNewEmail) {
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value())
            respond([message: 'Please select a user and enter new & confirmation email.'])
            return
        }

        params.newEmail = params.newEmail.toLowerCase()
        params.confirmNewEmail = params.confirmNewEmail.toLowerCase()

        if (params.newEmail != params.confirmNewEmail) {
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value())
            respond([message: 'Email dose not match the Confirm Email.'])
            return
        }

        if (User.countByEmail(params.newEmail)) {
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value())
            respond([message: "User already exists with Email: $params.newEmail"])
            return
        }

        User userInstance = User.get(params.id)
        if (!userInstance) {
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value())
            respond([message: "User not found with id: $params.id."])
            return
        }

        userInstance.email = params.newEmail
        if (!NucleusUtils.save(userInstance, true)) {
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value())
            log.warn "Error saving $userInstance $userInstance.errors"
            respond([message: "Unable to update user's email.", error: userInstance.errors])
            return
        }
        respond([message: 'Email updated Successfully.'])
    }
}
