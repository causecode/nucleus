package com.causecode.user

import grails.converters.JSON
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.plugin.springsecurity.SpringSecurityUtils
import spock.lang.*

@TestFor(UserManagementController)
@Mock([User, Role, UserRole])
class UserManagementControllerSpec extends Specification {

    void "test Index action with date filter applied"() {
        given:

        User adminUser = new User([username : "admin", password: "admin@13", email: "bootstrap@causecode.com",
                              firstName: "CauseCode", lastName: "Technologies", gender: "male", enabled: true])
        def springSecurityServiceForAdminUser = new Object()
        springSecurityServiceForAdminUser.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        adminUser.springSecurityService = springSecurityServiceForAdminUser
        assert  adminUser.save(flush: true)

        List<User> userInstanceList = [adminUser]

        controller.userManagementService = [listForMysql: { Map params ->
            return [instanceList: userInstanceList, totalCount: userInstanceList.size()]
        }] as UserManagementService

        when: "Index action is called"
        controller.index(15, 0, "Mysql")

        then: "List of users will be returned"
        response.json["instanceList"] != null
        response.json.instanceList[0].id
        response.json["totalCount"] != null
        response.json.totalCount == 1
        response.json["roleList"] != null

        cleanup:
        SpringSecurityUtils.metaClass = null;
    }

    void "test if an ADMIN user tries to Modify another ADMIN user's role"() {
        given: "ADMIN user logged-in for role modification"

        User managerUser = new User([username : "admin", password: "admin@13", email: "bootstrap@causecode.com",
                                     firstName: "CauseCode", lastName: "Technologies", gender: "male", enabled: true])
        def springSecurityService = new Object()
        springSecurityService.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        managerUser.springSecurityService = springSecurityService
        assert managerUser.save(flush: true)

        Role adminRole = Role.findOrSaveByAuthority("ROLE_ADMIN")
        assert adminRole.save(flush: true)
        Role userManagerRole = Role.findOrSaveByAuthority("ROLE_USER_MANAGER")
        assert userManagerRole.save(flush: true)
        Role userRole = Role.findOrSaveByAuthority("ROLE_USER")
        assert userRole.save(flush: true)
        //
        UserRole.create(managerUser, adminRole, true)
        controller.request.json = [
                userIds       : [managerUser.id],
                roleIds       : [adminRole.id, userManagerRole.id, userRole.id],
                roleActionType: "refresh"
        ] as JSON
        controller.request.method = "POST"
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        int index = 1
        controller.userManagementService = [getAppropiateIdList: { List ids ->
            if (index == 1) {
                index++
                return [managerUser.id]
            } else {
                return [adminRole.id]
            }
        }] as UserManagementService

        when: "Admin tries to modify other ADMIN user's role in Refresh mode"
        controller.modifyRoles()

        then: "He should be allowed to do so in virtue"
        controller.response.status == 200       // Successful execution of the action
        Set<Role> adminAuthorities = managerUser.getAuthorities()
        adminRole in adminAuthorities
        // Previous roles are wiped off
        userManagerRole in adminAuthorities == false
        userRole in adminAuthorities == false

        cleanup:
        SpringSecurityUtils.metaClass = null;
        UserRole.remove(managerUser, adminRole, true)
    }

    void "test if a Non-ADMIN user is trying to Modify other users' roles"() {
        given: "Request for Role modification in Refresh mode"

        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }

        User adminUser = new User([username : "admin", password: "admin@13", email: "bootstrap@causecode.com",
                                   firstName: "CauseCode", lastName: "Technologies", gender: "male", enabled: true])
        def springSecurityService = new Object()
        springSecurityService.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        adminUser.springSecurityService = springSecurityService
        assert adminUser.save(flush: true)

        User normalUser = new User([username : "normal", password: "normal@13", email: "normalbootstrap@causecode.com",
                                    firstName: "normalCauseCode", lastName: "normalTechnologies", gender: "male", enabled: true])
        def springSecurityServiceNormal = new Object()
        springSecurityServiceNormal.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        normalUser.springSecurityService = springSecurityServiceNormal
        assert normalUser.save(flush: true)

        User trialUser = new User([username : "trial", password: "trial@13", email: "trialbootstrap@causecode.com",
                                   firstName: "trialCauseCode", lastName: "trialTechnologies", gender: "male", enabled: true])
        def springSecurityServiceTrial = new Object()
        springSecurityServiceTrial.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        trialUser.springSecurityService = springSecurityServiceTrial
        assert trialUser.save(flush: true)

        Role adminRole = Role.findOrSaveByAuthority("ROLE_ADMIN")
        assert adminRole.save(flush: true)
        Role userManagerRole = Role.findOrSaveByAuthority("ROLE_USER_MANAGER")
        assert userManagerRole.save(flush: true)
        Role userRole = Role.findOrSaveByAuthority("ROLE_USER")
        assert userManagerRole.save(flush: true)

        controller.request.json = [
                roleActionType: "refresh",
                userIds       : [adminUser.id, trialUser.id, normalUser.id],
                roleIds       : [userManagerRole.id]
        ]
        controller.request.method = "POST"

        controller.userManagementService = [getAppropiateIdList: { List ids ->
            return [adminUser.id, trialUser.id, normalUser.id]
        }] as UserManagementService

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
        userRole in trialUserAuthorities == true

        Set<Role> normalUserAuthorities = normalUser.getAuthorities()
        userManagerRole in normalUserAuthorities
        userRole in normalUserAuthorities == true

        cleanup:
        SpringSecurityUtils.metaClass = null
    }

    void "test makeUserActiveInactive() to see if a NON-ADMIN user is trying to change status of an ADMIN user"() {
        given: "Deactivation request for 2 non-admin and 1 Admin user"

        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }

        User adminUser = new User([username : "admin", password: "admin@13", email: "bootstrap@causecode.com",
                                   firstName: "CauseCode", lastName: "Technologies", gender: "male", enabled: true])
        def springSecurityService = new Object()
        springSecurityService.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        adminUser.springSecurityService = springSecurityService
        assert adminUser.save(flush: true)

        User normalUser = new User([username : "normal", password: "normal@13", email: "normalbootstrap@causecode.com",
                                    firstName: "normalCauseCode", lastName: "normalTechnologies", gender: "male", enabled: false])
        def springSecurityServiceNormal = new Object()
        springSecurityServiceNormal.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        normalUser.springSecurityService = springSecurityServiceNormal
        assert normalUser.save(flush: true)

        User trialUser = new User([username : "trial", password: "trial@13", email: "trialbootstrap@causecode.com",
                                   firstName: "trialCauseCode", lastName: "trialTechnologies", gender: "male", enabled: false])
        def springSecurityServiceTrial = new Object()
        springSecurityServiceTrial.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        trialUser.springSecurityService = springSecurityServiceTrial
        assert trialUser.save(flush: true)

        controller.request.json = [selectedIds: [adminUser.id, trialUser.id, normalUser.id], type: "inactive"]
        controller.request.method = "POST"

        controller.userManagementService = [getAppropiateIdList: { List ids ->
            return [adminUser.id, trialUser.id, normalUser.id]
        }] as UserManagementService

        when: "Action is called"
        controller.makeUserActiveInactive()

        then: "Admin users must be removed from the current ID list"
        // 'refresh' action will fetch the updated values from the database
        adminUser.refresh().enabled == true   // ADMIN user Not deactivated
        trialUser.refresh().enabled == false
        normalUser.refresh().enabled == false

        cleanup:
        SpringSecurityUtils.metaClass = null;
    }

    void "test makeUserActiveInactive() if ADMIN user changes activation status of other ADMIN User"() {
        given: "Admin user to perform Role modification"
        // USER_MANAGER with Admin role is currently logged in
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }

        User adminUser = new User([username : "admin", password: "admin@13", email: "bootstrap@causecode.com",
                                   firstName: "CauseCode", lastName: "Technologies", gender: "male", enabled: false])
        def springSecurityService = new Object()
        springSecurityService.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        adminUser.springSecurityService = springSecurityService
        assert adminUser.save(flush: true)

        User normalUser = new User([username : "normal", password: "normal@13", email: "normalbootstrap@causecode.com",
                                    firstName: "normalCauseCode", lastName: "normalTechnologies", gender: "male", enabled: false])
        def springSecurityServiceNormal = new Object()
        springSecurityServiceNormal.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        normalUser.springSecurityService = springSecurityServiceNormal
        assert normalUser.save(flush: true)

        User trialUser = new User([username : "trial", password: "trial@13", email: "trialbootstrap@causecode.com",
                                   firstName: "trialCauseCode", lastName: "trialTechnologies", gender: "male", enabled: false])
        def springSecurityServiceTrial = new Object()
        springSecurityServiceTrial.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        trialUser.springSecurityService = springSecurityServiceTrial
        assert trialUser.save(flush: true)

        and: "Request with Admin and Normal user Ids for deactivation"
        controller.request.json = [selectedIds: [adminUser.id, trialUser.id, normalUser.id], type: "inactive"]


        controller.userManagementService = [getAppropiateIdList: { List ids ->
            return [adminUser.id, trialUser.id, normalUser.id]
        }] as UserManagementService

        User.metaClass.'static'.executeUpdate = { String query, Map parameters ->
            return
        }

        when: "Action is called"
        controller.makeUserActiveInactive()

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

        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }

        controller.userManagementService = [getAppropiateIdList: { List ids ->
            return []
        }] as UserManagementService

        when: "Action is called"
        controller.makeUserActiveInactive()

        then: "Action should be aborted and error message should be sent"
        controller.response.json.success == false
        controller.response.json.message.contains("Please select atleast one user.")

        cleanup:
        SpringSecurityUtils.metaClass = null;
    }

    void "test if selectedUserId list is blank in modifyRoles action"() {
        when: "Selected Ids are empty or Id is removed because of ADMIN permission"

        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }

        Role userManagerRole = Role.findOrSaveByAuthority("ROLE_USER_MANAGER")
        assert userManagerRole.save(flush: true)
        Role adminRole = Role.findOrSaveByAuthority("ROLE_ADMIN")
        assert adminRole.save(flush: true)

        controller.request.json = [userIds: [], roleIds: [userManagerRole.id, adminRole.id]]

        controller.userManagementService = [getAppropiateIdList: { List ids ->
            return
        }] as UserManagementService

        and: "Modify action is called"
        controller.modifyRoles()

        then: "Error message is thrown"
        controller.response.json.success == false
        controller.response.json.message.contains("Please select atleast one user.")

        cleanup:
        SpringSecurityUtils.metaClass = null;
    }

    void "test modifyRole() if RoleIds are removed during authentication"() {
        given: "Admin role id for modification"

        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }

        User normalUser = new User([username : "normal", password: "normal@13", email: "normalbootstrap@causecode.com",
                                    firstName: "normalCauseCode", lastName: "normalTechnologies", gender: "male", enabled: true])
        def springSecurityServiceNormal = new Object()
        springSecurityServiceNormal.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        normalUser.springSecurityService = springSecurityServiceNormal
        assert normalUser.save(flush: true)

        User trialUser = new User([username : "trial", password: "trial@13", email: "trialbootstrap@causecode.com",
                                   firstName: "trialCauseCode", lastName: "trialTechnologies", gender: "male", enabled: true])
        def springSecurityServiceTrial = new Object()
        springSecurityServiceTrial.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        trialUser.springSecurityService = springSecurityServiceTrial
        assert trialUser.save(flush: true)

        Role adminRole = Role.findOrSaveByAuthority("ROLE_ADMIN")
        assert adminRole.save(flush: true)
        Role userManagerRole = Role.findOrSaveByAuthority("ROLE_USER_MANAGER")
        assert userManagerRole.save(flush: true)
        Role userRole = Role.findOrSaveByAuthority("ROLE_USER")
        assert userRole.save(flush: true)

        controller.request.json = [
                userIds: [trialUser.id, normalUser.id],
                roleIds: [adminRole.id, userManagerRole.id, userRole.id]
        ]

        controller.request.method = "POST"

        int index = 1
        controller.userManagementService = [getAppropiateIdList: { List ids ->
            if (index == 1) {
                index++
                return [trialUser.id]
            } else {
                return //[adminRole.id]
            }
        }] as UserManagementService

        when: "Manager tries to set Admin Role to 2 Normal users"
        controller.modifyRoles()

        then: "Admin role shouldn't be applied to the 2 Normal Users"
        controller.response.json.success == false
        controller.response.json.message.contains("No Roles selected.")

        cleanup:
        SpringSecurityUtils.metaClass = null;
    }

    void "test modifyRole() if ADMIN user tries to assign Admin RoleIds in Refresh mode"() {
        given: "Admin roleId for role modification"

        User trialUser = new User([username : "trial", password: "trial@13", email: "trialbootstrap@causecode.com",
                                   firstName: "trialCauseCode", lastName: "trialTechnologies", gender: "male", enabled: true])
        def springSecurityServiceTrial = new Object()
        springSecurityServiceTrial.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        trialUser.springSecurityService = springSecurityServiceTrial
        assert trialUser.save(flush: true)

        User normalUser = new User([username : "normal", password: "normal@13", email: "normalbootstrap@causecode.com",
                                    firstName: "normalCauseCode", lastName: "normalTechnologies", gender: "male", enabled: true])
        def springSecurityServiceNormal = new Object()
        springSecurityServiceNormal.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        normalUser.springSecurityService = springSecurityServiceNormal
        assert normalUser.save(flush: true)

        Role adminRole = Role.findOrSaveByAuthority("ROLE_ADMIN")
        assert adminRole.save(flush: true)
        Role userRole = Role.findOrSaveByAuthority("ROLE_USER")
        assert userRole.save(flush: true)

        controller.request.json = [roleIds: [adminRole.id], userIds: [normalUser.id, trialUser.id],
                                   roleActionType: "refresh"]
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }

        int index = 1
        controller.userManagementService = [getAppropiateIdList: { List ids ->
            if (index == 1) {
                index++
                return [normalUser.id,trialUser.id]
            } else {
                return [adminRole.id]
            }
        }] as UserManagementService

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

        User normalUser = new User([username : "normal", password: "normal@13", email: "normalbootstrap@causecode.com",
                                    firstName: "normalCauseCode", lastName: "normalTechnologies", gender: "male", enabled: true])
        def springSecurityServiceNormal = new Object()
        springSecurityServiceNormal.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        normalUser.springSecurityService = springSecurityServiceNormal
        assert normalUser.save(flush: true)

        User trialUser = new User([username : "trial", password: "trial@13", email: "trialbootstrap@causecode.com",
                                   firstName: "trialCauseCode", lastName: "trialTechnologies", gender: "male", enabled: true])
        def springSecurityServiceTrial = new Object()
        springSecurityServiceTrial.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        trialUser.springSecurityService = springSecurityServiceTrial
        assert trialUser.save(flush: true)

        Role adminRole = Role.findOrSaveByAuthority("ROLE_ADMIN")
        assert adminRole.save(flush: true)
        Role userRole = Role.findOrSaveByAuthority("ROLE_USER")
        assert userRole.save(flush: true)

        controller.request.json = [roleIds: [adminRole.id], userIds: [normalUser.id, trialUser.id],
                                   roleActionType: "refresh"]
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }

        int index = 1
        controller.userManagementService = [getAppropiateIdList: { List ids ->
            if (index == 1) {
                index++
                return [normalUser.id,trialUser.id]
            } else {
                return [adminRole.id,userRole.id]
            }
        }] as UserManagementService

        when: "Admin tries to set Admin Role to 2 Normal users"
        controller.modifyRoles()

        then: "Admin role shouldn't be applied to the 2 Normal Users"
        controller.response.json.success == true

        // Already assigned roles also exists"
        Set<Role> trialAuthorities = trialUser.getAuthorities()
        adminRole in trialAuthorities
        userRole in trialAuthorities

        Set<Role> userAuthorities = normalUser.getAuthorities()
        adminRole in userAuthorities
        userRole in userAuthorities

        cleanup:
        SpringSecurityUtils.metaClass = null;
    }
}

