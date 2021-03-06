package com.causecode.geo.location

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * This class specifies test cases for {@link com.causecode.geo.location.City}.
 */
@Mock(Country)
@TestFor(City)
class CitySpec extends Specification {

    void 'test toString(List fields) method'() {
        given: 'List of valid fields for city'
        Country india = new Country([name: 'India'])
        List fields = ['country', 'city', 'state', 'stateCode']

        when: 'toString(List fields) method is called with invalid country value'
        City pune = new City([country: null, city: 'Pune', state: 'Maharashtra', stateCode: 'MH'])
        String result = pune.toString(fields)

        then: 'Result must match the provided string value, result will not contain India'
        result == 'Pune, Maharashtra, MH'

        when: 'toString(List fields method is called) with valid country value'
        City newCity = new City([country: india, city: 'Pune', state: 'Maharashtra', stateCode: 'MH'])
        result = newCity.toString(fields)

        then: 'Result must match the provided string value, result will contain India'
        result == 'Country(India), Pune, Maharashtra, MH'
    }

    void 'test toString method'() {
        when: 'toString method is called by cityInstance'
        Country india = new Country([name: 'India', code: 'IND'])
        assert india.save(flush: true, failOnError: true)
        City city = new City([country: india])
        assert city.save(flush: true, failOnError: true)
        String result = city

        then: 'It must match with the provided string value'
        result == 'City(1)'
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
