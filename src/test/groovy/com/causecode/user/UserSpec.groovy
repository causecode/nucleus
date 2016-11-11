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

@TestFor(User)
@Mock(SpringSecurityService)
class UserSpec extends Specification {

    def 'test email update'() {
        given: 'An email address already stored in database'
        User adminUser = new User(firstName: 'admin', lastName: 'admin', username: 'admin', password: 'admin@123', email: 'admin@causecode.com')

        SpringSecurityService springSecurityServiceForAdminUser = new SpringSecurityService()
        springSecurityServiceForAdminUser.metaClass.encodePassword = { String password -> 'ENCODED_PASSWORD' }
        adminUser.springSecurityService = springSecurityServiceForAdminUser
        assert adminUser.save(flush: true)

        when: 'Email for user is updated'
        adminUser.email = 'ADMIN@CAUSECODE.COM'
        adminUser.save(flush: true)

        then: 'Before update method is called'
        adminUser.email == 'admin@causecode.com'
    }

    @Unroll("person #field is #test using #value")
    void 'test user gender constraints'() {
        when: 'User instance is created with provided gender values'
        User userInstance = new User(firstName: 'admin', lastName: 'admin', username: 'admin', password: 'admin@123', email: 'admin@causecode.com', "$field": value)

        then: 'User instance is validated against constraints'
        validateConstraints(userInstance, field, test)

        where:
        test     | field    | value
        'inList' | 'gender' | 'Unknown'
        'valid'  | 'gender' | null
        'valid'  | 'gender' | ''
        'valid'  | 'gender' | 'male'
        'valid'  | 'gender' | 'female'
    }

    @Shared Date date = new Date().clearTime()
    @Unroll('User #field is #test using #value')
    void 'test birth date constraints'() {

        when: 'User instance is created with provided birthdate'
        User userInstance = new User(firstName: 'admin', lastName: 'admin', username: 'admin', password: 'admin', email: 'admin@causecode.com', "$field": value)

        then: 'User instance is validated against constraints'
        validateConstraints(userInstance, field, test)

        where:
        test      | field       | value
        'invalid' | 'birthdate' | date + 1
        'valid'   | 'birthdate' | null
        'valid'   | 'birthdate' | '  '
        'valid'   | 'birthdate' | date - 100
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
