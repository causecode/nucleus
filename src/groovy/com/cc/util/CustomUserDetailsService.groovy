/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.util

import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUser
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUserDetailsService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.dao.DataAccessException
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

import com.cc.user.Role
import com.cc.user.User

class CustomUserDetailsService implements GrailsUserDetailsService {

    static final List NO_ROLES = [new GrantedAuthorityImpl(SpringSecurityUtils.NO_ROLE)]

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        User.withTransaction {
            User userInstance = User.findByUsernameOrEmail(username, username)
            if(!userInstance)
                throw new UsernameNotFoundException("User not found", username)

            def authorities = userInstance.authorities.collect { Role roleInstance ->
                new GrantedAuthorityImpl(roleInstance.authority)
            }
            new GrailsUser(userInstance.username, userInstance.password, userInstance.enabled, !userInstance.accountExpired,
                    !userInstance.passwordExpired, !userInstance.accountLocked, authorities ?: NO_ROLES, userInstance.id)
        }
    }

    @Override
    UserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException, DataAccessException {
        loadUserByUsername(username)
    }

}