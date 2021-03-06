package com.causecode.geo.location

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * This class specifies unit test cases for {@link com.causecode.geo.location.Location}.
 */
@Mock([City, Country])
@TestFor(Location)
class LocationSpec extends Specification {

    void 'test getFullAddress() method'() {
        given: 'Location instance'
        Country india = new Country([name: 'India', code: 'IND'])
        assert india.save(flush: true, failOnError: true)

        City pune = new City([city: 'Pune', state: 'Maharashtra', stateCode: 'MH', country: india])
        assert pune.save(flush: true, failOnError: true)

        Location location = new Location([city: pune, address: 'Baner', zip: '343434'])
        assert location.save(flush: true, failOnError: true)

        when: 'getFullAddress method is called'
        String fullAddress = location.fullAddress

        then: 'returned fullAddress must match the given value'
        fullAddress == 'Baner, Pune, Maharashtra, India - 343434'

        when: 'getString method is called'
        String result = location

        then: 'It must match with the provided string value'
        result == 'Location(1)'
    }
}
