/*
 * Copyright (c) 2011-Present CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.geo.location

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import spock.lang.Specification

@Mock([City, Country])
@TestFor(Location)
@TestMixin(DomainClassUnitTestMixin)
class LocationSpec extends Specification {

    def 'test getFullAddress() method'() {
        given: 'Location instance'

        Country india = new Country(name: 'India', code: 'IND')
        assert india.save(flush: true, failOnError: true)

        City pune = new City(city: 'Pune', state: 'Maharashtra', stateCode: 'MH', country: india)
        assert pune.save(flush: true, failOnError: true)

        Location location = new Location(city: pune, address: 'Baner', zip: '343434')
        assert location.save(flush: true, failOnError: true)

        when: 'getFullAddress method is called'
        String fullAddress = location.getFullAddress()

        then: 'returned fullAddress must match the given value'
        fullAddress == 'Baner, Pune, Maharashtra, India - 343434'
    }

    def 'test toString method'() {
        when: 'toString method is called by location instance'
        Country india = new Country(code: 'IND', name: 'India')
        assert india.save(flush: true, failOnError: true)

        City pune = new City(city: 'Pune', country: india)
        assert pune.save(flush: true, failOnError: true)

        Location location = new Location(city: pune, address: 'Baner', zip: '343443')
        assert location.save(flush: true, failOnError: true)

        String result = location.toString()

        then: 'It must match with the provided string value'
        result == 'Location [1]'
    }
}
