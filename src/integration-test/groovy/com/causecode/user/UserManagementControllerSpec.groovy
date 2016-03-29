package com.causecode.user

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import nucleus.BaseIntegrationSpec

import org.springframework.beans.factory.annotation.*
import org.springframework.security.core.context.SecurityContextHolder

import spock.lang.*

class UserManagementControllerSpec extends BaseIntegrationSpec {

    def userManagementService
    SpringSecurityService springSecurityService

    def setup() {
        controller = new UserManagementController()
    }

    void "test Index action with date filter applied"() {
        when: "Index action is called"
        controller.index(15, 0, "Mysql")

        then: "List of users will be returned"
        controller.response.json["instanceList"] != null
        controller.response.json.instanceList[0].id
        controller.response.json["totalCount"] != null
        controller.response.json.totalCount == 4
        controller.response.json["roleList"] != null
    }

    void "test if an ADMIN user tries to Modify another ADMIN user's role"() {
        given: "ADMIN user logged-in for role modification"
        UserRole.create(managerUser, adminRole, true)
        controller.request.json = [userIds: [managerUser.id], roleIds : [userRole.id], roleActionType: "refresh"]
        controller.request.method = "POST"
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }

        when: "Admin tries to modify other ADMIN user's role in Refresh mode"
        controller.modifyRoles()

        then: "He should be allowed to do so in virtue"
        controller.response.status == 200       // Successful execution of the action
        Set<Role> adminAuthorities = managerUser.getAuthorities()
        userRole in adminAuthorities
        // Previous roles are wiped off
        userManagerRole in adminAuthorities == false
        adminRole in adminAuthorities == false

        cleanup:
        SpringSecurityUtils.metaClass = null;
        UserRole.remove(managerUser, adminRole, true)
    }

    void "test if a Non-ADMIN user is trying to Modify other users' roles"() {
        given: "Request for Role modification in Refresh mode"
        controller.request.json = [roleActionType: "refresh", userIds: [adminUser.id, trialUser.id , normalUser.id], 
            roleIds : [userManagerRole.id]]
        controller.request.method = "POST"
        
        when: "Logged-in user selects 1 ADMIN and 2 normal users for role modification"
        controller.modifyRoles()

        then: "Admin users must be removed from the ID list and Normal User's roles should be updated"
        Set<Role> adminAuthorities = adminUser.getAuthorities()
        // No role modification done for Admin user
        userManagerRole in adminAuthorities
        adminRole in adminAuthorities
        userRole in adminAuthorities

        Set<Role> trialUserAuthorities = trialUser.getAuthorities()
        userManagerRole in trialUserAuthorities
        userRole in trialUserAuthorities == false

        Set<Role> normalUserAuthorities = normalUser.getAuthorities()
        userManagerRole in normalUserAuthorities
        userRole in normalUserAuthorities == false
    }

    void "test makeUserActiveInactive() to see if a NON-ADMIN user is trying to change status of an ADMIN user"() {
        given: "Deactivation request for 2 non-admin and 1 Admin user"
        controller.request.json = [selectedIds: [adminUser.id, trialUser.id , normalUser.id], type: false]
        controller.request.method = "POST"

        when: "Action is called"
        controller.makeUserActiveInactive()
        flushSession()

        then: "Admin users must be removed from the current ID list"
        // 'refresh' action will fetch the updated values from the database
        adminUser.refresh().enabled == true   // ADMIN user Not deactivated
        trialUser.refresh().enabled == false
        normalUser.refresh().enabled == false
    }

    void "test makeUserActiveInactive() if ADMIN user changes activation status of other ADMIN User"() {
        given:"Admin user to perform Role modification"
        // USER_MANAGER with Admin role is currently logged in
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }

        and: "Request with Admin and Normal user Ids for deactivation"
        controller.request.json = [selectedIds: [adminUser.id, trialUser.id , normalUser.id], type: false]

        when: "Action is called"
        controller.makeUserActiveInactive()
        flushSession()

        then: "Deactivaion is allowed on all Users"
        adminUser.refresh().enabled == false
        trialUser.refresh().enabled == false
        normalUser.refresh().enabled == false
        controller.response.json.success == true

        cleanup:
        SpringSecurityUtils.metaClass = null;
    }

    void "test makeUserActiveInactive() when no user Id is selected"() {
        given: "Blank user id list or Admin Ids removed"
        controller.request.json = [selectedIds: []]

        when: "Action is called"
        controller.makeUserActiveInactive()

        then: "Action should be aborted and error message should be sent"
        controller.response.json.success == false
        controller.response.json.message.contains("Please select atleast one user.")
    }

    void "test if selectedUserId list is blank in modifyRoles action"() {
        when: "Selected Ids are empty or Id is removed because of ADMIN permission"
        controller.request.json = [userIds: [], roleIds: [userManagerRole.id, adminRole.id]]
        
        and: "Modify action is called"
        controller.modifyRoles()

        then: "Error message is thrown"
        controller.response.json.success == false
        controller.response.json.message.contains("Please select atleast one user.")
    }

    void "test modifyRole() if RoleIds are removed during authentication"() {
        given: "Admin role id for modification"
        controller.request.json = [roleIds: [adminRole.id], userIds: [normalUser.id, trialUser.id]]

        when: "Manager tries to set Admin Role to 2 Normal users"
        controller.modifyRoles()

        then: "Admin role shouldn't be applied to the 2 Normal Users"
        controller.response.json.success == false
        controller.response.json.message.contains("No Roles selected.")
    }

    void "test modifyRole() if ADMIN user tries to assign Admin RoleIds in Refresh mode"() {
        given: "Admin roleId for role modification"
        controller.request.json = [roleIds: [adminRole.id], userIds: [normalUser.id, trialUser.id], 
            roleActionType : "refresh"]
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }

        when: "Admin tries to set Admin Role to 2 Normal users"
        controller.modifyRoles()

        then: "Only Admin role should be applied to the 2 Normal Users"
        controller.response.json.success == true

        Set<Role> trialUserAuthorities = trialUser.getAuthorities()
        adminRole in trialUserAuthorities
        userRole in trialUserAuthorities == false

        Set<Role> normalUserAuthorities = normalUser.getAuthorities()
        adminRole in normalUserAuthorities
        userRole in normalUserAuthorities == false

        cleanup:
        SpringSecurityUtils.metaClass = null;
    }

    void "test modifyRole() if ADMIN user tries to assign Admin RoleIds in Append mode"() {
        given: "Admin role id for modification"
        controller.request.json = [roleIds: [adminRole.id], userIds: [normalUser.id, trialUser.id], 
            roleActionType : "append"]
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }

        when: "Admin tries to set Admin Role to 2 Normal users"
        controller.modifyRoles()

        then: "Admin role shouldn be applied to the 2 Normal Users"
        controller.response.json.success == true

        // Already assigned roles also exists"
        Set<Role> trialAuthorities = trialUser.getAuthorities()
        adminRole in trialAuthorities
        userRole in trialAuthorities

        Set<Role> Userauthorities = normalUser.getAuthorities()
        adminRole in Userauthorities
        userRole in Userauthorities

        cleanup:
        SpringSecurityUtils.metaClass = null;
    }
}