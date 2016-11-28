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
import spock.lang.Unroll

@Mock([PhoneCountryCode,Country])
@TestFor(Phone)
class PhoneSpec extends Specification {

    @Unroll("Phone #field is #test using #value")
    void 'test number field'() {
        when: 'Phone instance is created with provided value for number field'
        Phone phone = new Phone("$field" : value)

        then: 'Contact instance is validated against the constraint'
        validateConstraints(phone,field,test)

        where:
        test        | field    | value
        'nullable'  | 'number' | null
        'blank'     | 'number' | '  '
        'invalid'   | 'number' | '9876543'
        'invalid'   | 'number' | '98765432101'
        'valid'     | 'number' | '9876543210'
    }

    void 'test getFullPhoneNumber() '() {
        given: 'PhoneCountry instance'
        Country india = new Country([code: 'IND', name: 'India'])
        assert india.save(flush:true ,failOnError: true)

        PhoneCountryCode indiaCode = new PhoneCountryCode([code: '91', country: india])
        assert indiaCode.save(flush: true, failOnError:true)

        Phone phone = new Phone([countryCode: indiaCode, number: '9876543210'])

        when: 'getFullPhoneNumber() is called'
        String result = phone.getFullPhoneNumber()

        then:
        result == '+(91)9876543210'
    }

    void "test toString() method"() {
        given: 'Phone, PhoneCountryCode and Country instances'
        Country india = new Country([code: 'IND', name: 'India'])
        assert india.save(flush: true, failOnError: true)

        PhoneCountryCode phoneCountryCode = new PhoneCountryCode([code: '91',country:  india])
        assert phoneCountryCode.save(flush: true, failOnError: true)

        Phone phoneInstance = new Phone([number: '9876543210', countryCode: phoneCountryCode])
        assert phoneInstance.save(flush: true, failOnError: true)

        when: 'toString is called for country instance'
        String result = india.toString()

        then: 'result must match with the provided string value'
        result == 'Country(India)'

        when: 'toString is called for phoneCountryCode instance'
        result = phoneCountryCode.toString()

        then: 'result must match with the provided string value'
        result == 'PhoneCountryCode(1)'

        when: 'toString is called for phone instance'
        result = phoneInstance.toString()

        then: 'result must match the provided string value'
        result == 'Phone(1)'
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
