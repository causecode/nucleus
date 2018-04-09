import grails.core.GrailsApplication
import grails.util.Environment

import com.causecode.user.Role
import com.causecode.user.User
import com.causecode.user.UserRole
import org.apache.commons.lang.RandomStringUtils

/**
 * Contains configuration for development mode.
 */
@SuppressWarnings(['Instanceof'])
class NucleusDevBootStrap {

    GrailsApplication grailsApplication

    def init = { servletContext  ->
        if (Environment.isDevelopmentMode()) {
            def executeBootstrap = grailsApplication.config.app.executeDevBootstrap

            if (executeBootstrap instanceof Boolean && !executeBootstrap) {
                log.debug 'Not executing Nucleus Development bootstap.'

                return
            }

            log.debug 'Nucleus Development bootstrap executing.'

            Role userRole = Role.findOrSaveByAuthority('ROLE_USER')
            Role adminRole = Role.findOrSaveByAuthority('ROLE_ADMIN')
            Role userManagerRole = Role.findOrSaveByAuthority('ROLE_USER_MANAGER')

            User adminUser = User.findByUsernameAndEnabled('admin', true)
            User normalUser = User.findByUsername('user')

            Map failOnError = [failOnError: true]

            if (!adminUser) {
                adminUser = new User([username : 'admin', password: RandomStringUtils.randomAlphanumeric(16),
                        email: 'admin@mycompany.com', enabled: true,])
                adminUser.save(failOnError)
            }

            if (!normalUser) {
                normalUser = new User([username: 'user', password: RandomStringUtils.randomAlphanumeric(16),
                        email: 'user@mycompany.com', enabled: true])
                normalUser.save(failOnError)
            }

            UserRole.findOrSaveByUserAndRole(adminUser, adminRole)
            UserRole.findOrSaveByUserAndRole(adminUser, userRole)
            UserRole.findOrSaveByUserAndRole(adminUser, userManagerRole)
            UserRole.findOrSaveByUserAndRole(normalUser, userRole)

            log.debug 'Nucleus Development bootstrap finished executing.'
        }
    }
}
