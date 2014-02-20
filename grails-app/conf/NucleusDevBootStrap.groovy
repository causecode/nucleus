import grails.util.Environment

import com.cc.user.Role
import com.cc.user.User
import com.cc.user.UserRole

class NucleusDevBootStrap {

    def grailsApplication

    def init = { servletContext  ->
        if(Environment.isDevelopmentMode()) {
            def executeBootstrap = grailsApplication.config.app.executeDevBootstrap
            if(executeBootstrap instanceof Boolean && !executeBootstrap.toBoolean()) {
                log.debug "Not executing Nucleus Development bootstap."
                return
            }

            log.debug "Nucleus Development bootstrap executing."

            Role userRole = Role.findOrSaveByAuthority("ROLE_USER")
            Role adminRole = Role.findOrSaveByAuthority("ROLE_ADMIN")

            User adminUser = User.findByUsernameAndEnabled("admin", true)
            User normalUser = User.findByUsername("jane")

            if(!adminUser) {
                adminUser = new User([username: "admin", password: "admin@13", email: "bootstrap@causecode.com",
                    firstName: "CauseCode", lastName: "Technologies", gender: "male", enabled: true,])
                adminUser.save(failOnError: true)
            }

            if(!normalUser) {
                normalUser = new User([username: "jane", password: "admin@13", email:"jane@causecode.com",
                    firstName: "Jane", lastName: "Doe", gender: "female", enabled: true])
                normalUser.save(failOnError: true)
            }
            UserRole.findOrSaveByUserAndRole(adminUser, adminRole)
            UserRole.findOrSaveByUserAndRole(adminUser, userRole)
            UserRole.findOrSaveByUserAndRole(normalUser, userRole)

            log.debug "Nucleus Development bootstrap finished executing."
        }
    }
}