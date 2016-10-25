/*
 * Copyright (c) 2011-Present CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.contact

import com.causecode.geo.location.City
import com.causecode.geo.location.Country
import com.causecode.geo.location.Location
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

@Mock([Location, City, Country])
@TestFor(Contact)
class ContactSpec extends Specification {

    def 'test toString() method'() {
        when: 'Contact instance is created and toString is called'
        Country india = new Country(code: 'IND', name: 'India')
        assert india.save(flush: true, failOnError: true)

        City city = new City(city: 'Pune', state: 'Maharashtra', stateCode: 'MH', country: india)
        assert city.save(flush: true, failOnError: true)

        Location location = new Location(name: 'Home Address', address: 'somewhere on earth', zip: '123456', city: city)
        assert location.save(flush: true, failOnError: true)

        Contact contactInstance = new Contact(address: location)
        assert contactInstance.save(flush: true, failOnError: true)
        String result = contactInstance.toString()

        then: 'result must match with given string'
        result == 'Contact [1]'
    }
}
