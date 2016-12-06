/*
 * Copyright (c) 2016, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

import grails.core.GrailsApplication
import grails.util.Environment

import com.causecode.user.Role
import com.causecode.user.User
import com.causecode.user.UserRole

/**
 * Contains configuration for development mode of Nucleus
 */
@SuppressWarnings(['Instanceof'])
class NucleusDevBootStrap {

    GrailsApplication grailsApplication

    def init = { servletContext  ->
        if (Environment.isDevelopmentMode()) {
            def executeBootstrap = grailsApplication.config.app.executeDevBootstrap
            if (executeBootstrap instanceof Boolean && !executeBootstrap.toBoolean()) {
                log.debug 'Not executing Nucleus Development bootstap.'
                return
            }

            log.debug 'Nucleus Development bootstrap executing.'

            Role userRole = Role.findOrSaveByAuthority('ROLE_USER')
            Role adminRole = Role.findOrSaveByAuthority('ROLE_ADMIN')
            Role userManagerRole = Role.findOrSaveByAuthority('ROLE_USER_MANAGER')

            User adminUser = User.findByUsernameAndEnabled('admin', true)
            User normalUser = User.findByUsername('jane')

            Map failOnError = [ failOnError: true]

            if (!adminUser) {
                adminUser = new User([username: 'admin', password: 'admin@13', email: 'bootstrap@causecode.com',
                        firstName: 'CauseCode', lastName: 'Technologies', gender: 'male', enabled: true,])
                adminUser.save(failOnError)
            }

            if (!normalUser) {
                normalUser = new User([username: 'jane', password: 'admin@13', email: 'jane@causecode.com',
                        firstName: 'Jane', lastName: 'Doe', gender: 'female', enabled: true])
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
