package com.causecode.user

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * This class specifies unit test cases for {@link com.causecode.user.Role}.
 */
@TestFor(Role)
class RoleSpec extends Specification {

    void 'test toString() method'() {
        when: 'UserRole instance is created and toString is called'
        Role role = new Role([authority: 'ROLE_ADMIN'])
        assert role.save(flush: true, failOnError: true)
        String result = role

        then: 'result must satisfy the following condition'
        result == 'Role(1)'
    }
}
