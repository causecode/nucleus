import grails.util.Environment

import com.cc.user.Role
import com.cc.user.User
import com.cc.user.UserRole

class NucleusDevBootStrap {

    def grailsApplication

    def init = { servletContext  ->
        if(Environment.current == Environment.DEVELOPMENT) {
            def executeBootstrap = grailsApplication.config.app.executeDevBootstrap
            if(executeBootstrap instanceof Boolean && !executeBootstrap.toBoolean()) {
                log.debug "Not executing Nucleus Development bootstap."
                return
            }

            log.debug "Nucleus Development bootstrap executing."

            Role userRole = Role.findOrSaveByAuthority("ROLE_USER")
            Role adminRole = Role.findOrSaveByAuthority("ROLE_ADMIN")

            User adminUser = User.findByUsernameAndEnabled("admin", true)
            User shashank = User.findByEmail("shashank.agrawal@causecode.com")

            if(!adminUser) {
                adminUser = new User([username: "admin", password: "admin@13", email:"vishesh@causecode.com", enabled: true,
                    firstName: "Vishesh", lastName: "Duggar", gender: "male"])
                adminUser.save(failOnError: true)
            }

            if(!shashank) {
                shashank = new User([username: "shashank.agrawal", password: "causecode.11", gender: "male",
                    email:"shashank.agrawal@causecode.com", enabled: true, firstName: "Shashank", lastName: "Agrawal"])
                shashank.save(failOnError: true)
            }
            UserRole.findOrSaveByUserAndRole(adminUser, adminRole)
            UserRole.findOrSaveByUserAndRole(adminUser, userRole)
            UserRole.findOrSaveByUserAndRole(shashank, userRole)

            log.debug "Nucleus Development bootstrap finished executing."
        }
    }
}