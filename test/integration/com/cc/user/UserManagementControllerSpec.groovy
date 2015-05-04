package com.cc.user



import grails.converters.JSON
import grails.test.spock.IntegrationSpec
import spock.lang.*
import org.springframework.http.HttpStatus;
import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import grails.transaction.Rollback
import grails.test.mixin.integration.Integration
import org.springframework.beans.factory.annotation.*

class UserManagementControllerSpec extends IntegrationSpec {

    def userManagementService
    User adminUser, normalUser, managerUser, trialUser
    Role userRole, adminRole, userManagerRole
    UserManagementController controller

    def setup() {
        controller = new UserManagementController()
        
        // Creating roles for each setup
        userRole = Role.findOrSaveByAuthority("ROLE_USER")
        adminRole = Role.findOrSaveByAuthority("ROLE_ADMIN")
        userManagerRole = Role.findOrSaveByAuthority("ROLE_USER_MANAGER")
        
        // Creating Users for each setup
       
        // Admin user
        adminUser = new User([username: "admin", password: "admin@13", email: "bootstrap@causecode.com",
            firstName: "CauseCode", lastName: "Technologies", gender: "male", enabled: true,])
        adminUser.save(flush: true)
        
        // Normal user
        normalUser = new User([username: "jane", password: "admin@13", email:"jane@causecode.com",
            firstName: "Jane", lastName: "Doe", gender: "female", enabled: true])
        normalUser.save(flush: true)
        
        // User manager
        managerUser = new User([username: "rachnac", password: "admin@13", email:"rachna@causecode.com",
            firstName: "Rachna", lastName: "Rathod", gender: "female", enabled: true])
        managerUser.save(flush: true)
        
        trialUser  = new User([username: "trial", password: "admin@13", email:"trial@causecode.com",
            firstName: "Trial", lastName: "Bug", gender: "female", enabled: true])
        trialUser.save(flush: true)
        
        // Creating user Role instances
        UserRole userRoleInstance1 = new UserRole([user: managerUser, role: userManagerRole])   // User Manager  (with a Non-Admin role)
        userRoleInstance1.save(flush:true)
        UserRole userRoleInstance2 = new UserRole([user: normalUser, role: userRole])   // General User
        userRoleInstance2.save(flush:true)
        UserRole userRoleInstance3 = new UserRole([user: trialUser, role: userRole])    // One more general user
        userRoleInstance3.save(flush:true)
        UserRole userRoleInstance4 = new UserRole([user: adminUser, role: adminRole])   // Admin user
        userRoleInstance4.save(flush:true)
        UserRole userRoleInstanc5 = new UserRole([user:managerUser, role: adminRole])    // User with Manager & Admin role
        userRoleInstanc5.save(flush:true)
    }
    
    void testIndexAction() {
        controller.userManagementService = userManagementService
        
        when: "Index action is called"
        controller.index(15, 0, "Mysql")
        
        then: "List of users will be returned"
        controller.response.json["instanceList"] != null
        controller.response.json["totalCount"] != null
        controller.response.json["roleList"] != null
    }

    void "test if an ADMIN user tries to modify another ADMIN user's role"() {
       
        given: "ADMIN user logged-in for role modification"
        controller.request.json = [userIds: [adminUser.id], roleIds : [userManagerRole.id]]
        controller.request.method = "POST"
        
        when: "Tries to modify other ADMIN user's role"
        controller.modifyRoles()

        then: "He should be allowed to do so in virtue"
        controller.response.status == 200       // Successful execution of the action
        UserRole.count() == 6
    }
    
    void "test if a Non-ADMIN user is trying to modify other users' roles"() {
        
        when: "Logged-in user selects 1 ADMIN and 2 normal users for role modification"
        controller.request.json = [userIds: [adminUser.id, trialUser.id , normalUser.id], roleIds : [userManagerRole.id]]
        controller.request.method = "POST"
        controller.modifyRoles()
        
        then: "Admin users must be removed from the current ID list"
        controller.response.status == 200       // Successful execution of method
        UserRole.count() == 7       // Admin user's role is not modified
    }

    void "test makeUserActiveInactive method to see if a NON-ADMIN user is trying to make active status for an ADMIN user"() {
        
        given: "Request with an in-activation request for 2 non-admin and 1 Admin user"
        controller.request.json = [selectedIds: [adminUser.id, trialUser.id , normalUser.id], type : ['inactive']]
        controller.request.method = "POST"
        
        when: "Logged in user selects ADMIN users as well from the list"
        controller.makeUserActiveInactive()
        
        then: "Admin users must be removed from the current ID list"
        controller.response.status == 200       // Successful execution of method
        // Check if Admin user's status not modified
        def adminUserForActivationStatus = User.get(adminUser.id)
        assert adminUserForActivationStatus.enabled == true
    }
    
    void "test makeUserActiveInactive action when no user Id is selected "() {
        given: "Blank user id list or Admin ids removed"
        controller.request.json = [selectedIds: []]
        
        when: "Action is called"
        controller.makeUserActiveInactive()
        
        then: "Action should be aborted and error message should be sent"
        assert controller.response.text.contains("Please select atleast one user")
    }

    void "test if selected user Ids list is blank in modifyRoles action"() {
        when: "Selected Ids are empty or Id is removed because of ADMIN permission"
        controller.request.json = [userIds: []]
        controller.modifyRoles()
        
        then: "Error message is thrown"
        assert controller.response.text.contains("Please select atleast one user")
    }
    
}