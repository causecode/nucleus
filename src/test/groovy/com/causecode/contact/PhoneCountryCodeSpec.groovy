/*
 * Copyright (c) 2011-Present CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.contact

import com.causecode.geo.location.Country
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

@Mock([Country])
@TestFor(PhoneCountryCode)
class PhoneCountryCodeSpec extends Specification {

    def 'test toString() method'() {
        when: 'PhoneCountryCode instance is created and toString is called'
        Country india = new Country(code: 'IND', name: 'India')
        assert india.save(flush: true, failOnError: true)

        PhoneCountryCode indiaCode = new PhoneCountryCode(code: '91', country: india)
        assert indiaCode.save(flush: true, failOnError: true)

        then:
        indiaCode.toString() == 'PhoneCountryCode [1]'
    }
}
