/*
 * Copyright (c) 2011-Present CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.geo.location

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(Country)
class CountrySpec extends Specification {

    def 'test toString method'() {
        when: 'toString method is called by countryInstance'
        Country india = new Country(name: 'India')
        assert india.save(flush: true, failOnError: true)
        String result = india.toString()

        then: 'It must match with the provided string value'
        result == 'India'
    }
}
