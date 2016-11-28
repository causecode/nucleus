/*
 * Copyright (c) 2011-Present CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.user

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(Role)
class RoleSpec extends Specification{

    void 'test toString() method'() {
        when: 'UserRole instance is created and toString is called'
        Role role = new Role([authority:'ROLE_ADMIN'])
        assert role.save(flush: true, failOnError: true)
        String result = role.toString()

        then: 'result must satisfy the following condition'
        result == 'Role(1)'
    }
}
