/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.util

import grails.plugin.springsecurity.userdetails.GrailsUser
import grails.plugin.springsecurity.userdetails.GrailsUserDetailsService
import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.dao.DataAccessException
import org.springframework.security.core.authority.GrantedAuthorityImpl
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

import com.cc.user.Role
import com.cc.user.User

/**
 * This service provides methods to get UserDetails by different fields.
 * @author Vishesh Duggar
 * @author Shashank Agrawal
 *
 */
class CustomUserDetailsService implements GrailsUserDetailsService {

    static final List NO_ROLES = [new GrantedAuthorityImpl(SpringSecurityUtils.NO_ROLE)]

    /**
     * Override method search user by username parameter passed and return user details.
     * @param username String value used to get user instance
     * @throws UsernameNotFoundException
     * @throws {@link DataAccessException}
     * @return GrailsUser for searched user instance. Throws exceptions if user not found or data access exception error occurs.
     */
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

    /**
     * Override method search user by username parameter passed and return user details.
     * @param username String value used to get user instance
     * @param loadRoles Boolean field
     * @throws UsernameNotFoundException
     * @throws {@link DataAccessException}
     * @return GrailsUser for searched user instance. Throws exceptions if user not found or data access exception error occurs.
     */
    @Override
    UserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException, DataAccessException {
        loadUserByUsername(username)
    }

}
