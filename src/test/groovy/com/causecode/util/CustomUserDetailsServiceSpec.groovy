/*
 * Copyright (c) 2011-Present CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.util

import com.causecode.user.Role
import com.causecode.user.User
import com.causecode.user.UserRole
import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification
import spock.util.mop.ConfineMetaClassChanges


@Mock([User, Role, UserRole, SpringSecurityService])
@TestFor(CustomUserDetailsService)
class CustomUserDetailsServiceSpec extends Specification {

    @ConfineMetaClassChanges(SpringSecurityService)
    void 'test loadUserByUsername method when user is found'() {
        given: 'User instance'
        User adminUser = new User([firstName: 'admin', lastName: 'admin', username: 'admin', password: 'admin@123',
                email: 'admin@causecode.com'])

        SpringSecurityService springSecurityServiceForAdminUser = new SpringSecurityService()
        springSecurityServiceForAdminUser.metaClass.encodePassword = { String password -> 'ENCODED_PASSWORD' }
        adminUser.springSecurityService = springSecurityServiceForAdminUser
        assert adminUser.save(flush: true, failOnError: true)

        Role adminRole = new Role(authority: 'ROLE_ADMIN')
        assert adminRole.save(flush: true, failOnError: true)

        String username = 'admin'

        CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService()
        when: 'loaduserByUsername method is called'
        UserDetails details = customUserDetailsService.loadUserByUsername(username,true)

        then: 'User instance is found and user details are returned'
        details != null
        details.username == username
    }

    void 'test loadUserByUsername method when user does not exist'() {
        given:'CustomUserDetailsService instance'
        CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService()

        when: 'loaduserByUsername method is called'
        customUserDetailsService.loadUserByUsername('admin')

        then: 'UsernameNotFound exception is thrown'
        thrown(UsernameNotFoundException)
    }
}
