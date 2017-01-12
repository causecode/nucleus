/*
 * Copyright (c) 2011 - Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.user

import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * This class specifies unit test cases for {@link com.causecode.user.User}.
 */
@TestFor(User)
@Mock(SpringSecurityService)
class UserSpec extends Specification {

    def 'test email update'() {
        given: 'An email address already stored in database'
        User adminUser = new User([firstName: 'admin', lastName: 'admin', username: 'admin', password: 'admin@123',
                email: 'admin@causecode.com'])

        SpringSecurityService springSecurityServiceForAdminUser = new SpringSecurityService()
        springSecurityServiceForAdminUser.metaClass.encodePassword = { String password -> 'ENCODED_PASSWORD' }
        adminUser.springSecurityService = springSecurityServiceForAdminUser
        assert adminUser.save(flush: true)

        when: 'Email for user is updated'
        adminUser.email = 'ADMIN@CAUSECODE.COM'
        adminUser.save(flush: true)

        then: 'beforeUpdate() method is called'
        adminUser.email == 'admin@causecode.com'
    }

    @Unroll('person gender is #test using #value')
    void 'test user gender constraints'() {
        when: 'User instance is created with provided gender values'
        User userInstance = new User([firstName: 'admin', lastName: 'admin', username: 'admin', password: 'admin@123',
                email: 'admin@causecode.com', 'gender': value])

        then: 'User instance is validated against constraints'
        validateConstraints(userInstance, 'gender', test)

        where:
        test     | value
        'inList' | 'Unknown'
        'valid'  | null
        'valid'  | ''
        'valid'  | 'male'
        'valid'  | 'female'
    }

    @Shared Date date = new Date().clearTime()
    @Unroll('User birthdate is #test using #value')
    void 'test birth date constraints'() {

        when: 'User instance is created with provided birthdate'
        User userInstance = new User([firstName: 'admin', lastName: 'admin', username: 'admin', password: 'admin',
        email: 'admin@causecode.com', 'birthdate': value])

        then: 'User instance is validated against constraints'
        validateConstraints(userInstance, 'birthdate', test)

        where:
        test      | value
        'invalid' | date + 1
        'valid'   | null
        'valid'   | '  '
        'valid'   | date - 100
    }

    void validateConstraints(obj, field, test) {
        def validated = obj.validate()
        if (test && test != 'valid') {
            assert !validated
            assert obj.errors[field]
        } else {
            assert !obj.errors[field]
        }
    }
}
