package nucleus

import com.cc.user.Role;
import com.cc.user.User
import com.cc.user.UserManagementController
import com.cc.user.UserRole

import grails.test.spock.IntegrationSpec

class BaseIntegrationSpec extends IntegrationSpec {
    User adminUser, normalUser, managerUser, trialUser
    Role userRole, adminRole, userManagerRole
    UserManagementController controller

    def setup() {
        // Creating roles for each setup
        userRole = Role.findOrSaveByAuthority("ROLE_USER")
        assert userRole.id

        adminRole = Role.findOrSaveByAuthority("ROLE_ADMIN")
        assert adminRole.id

        userManagerRole = Role.findOrSaveByAuthority("ROLE_USER_MANAGER")
        assert userManagerRole.id

        // Creating Users for each setup
        // Admin user
        adminUser = new User([username: "admin@1", password: "admin@13", email: "bootstrap1@causecode.com",
                              firstName: "CauseCode", lastName: "Technologies", gender: "male", enabled: true])
        adminUser.save(flush: true)
        UserRole.create(adminUser, adminRole, true)
        UserRole.create(adminUser, userRole, true)
        UserRole.create(adminUser, userManagerRole, true)
        assert adminUser.id

        // Normal user
        normalUser = new User([username: "jane@1", password: "admin@13", email:"janeCC@causecode.com",
                               firstName: "Jane", lastName: "Doe", gender: "female", enabled: true])
        normalUser.save(flush: true)
        UserRole.create(normalUser, userRole, true)
        assert normalUser.id

        // User manager
        managerUser = new User([username: "xxxx", password: "admin@13", email:"rachna1@causecode.com",
                                firstName: "XXX", lastName: "YYY", gender: "female", enabled: true])
        managerUser.save(flush: true)
        UserRole.create(managerUser, userManagerRole, true)
        UserRole.create(managerUser, userRole, true)
        assert managerUser.id

        trialUser = new User([username: "trial", password: "admin@13", email:"trial@causecode.com",
                              firstName: "Trial", lastName: "User", gender: "female", enabled: true])
        trialUser.save(flush: true)
        UserRole.create(trialUser, userRole, true)
        assert trialUser.id
    }
}
