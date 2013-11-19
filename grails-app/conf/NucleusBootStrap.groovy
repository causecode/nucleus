/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

import com.cc.user.Role
import com.cc.user.User
import com.cc.user.UserRole

class NucleusBootStrap {

    def init = { servletContext ->
        log.debug "Nucleus Bootstrap started executing .."
        Role userRole = Role.findOrSaveByAuthority("ROLE_USER")
        Role adminRole = Role.findOrSaveByAuthority("ROLE_ADMIN")
        User adminUser = User.findByUsername("admin")

        if(!adminUser) {
            log.debug "Admin user not found. Saving .."
            adminUser = new User([username: "admin", password: "causecode.11", firstName: "Vishesh", lastName: "Duggar",
                email: "vishesh@causecode.com", birthdate: "01/01/1985", gender: "male"])
            adminUser.save(failOnError: true)
            log.debug "Admin user saved."
        }

        UserRole.findOrSaveByUserAndRole(adminUser, adminRole)
        UserRole.findOrSaveByUserAndRole(adminUser, userRole)
        log.debug "Nucleus Bootstrap finished executing."
    }

}