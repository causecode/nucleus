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
import com.causecode.user.User
import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Specification

@Mock([User, Phone, PhoneCountryCode, Contact, Location, Country, City, SpringSecurityService])
@TestFor(ContactService)
class ContactServiceSpec extends Specification {

    ContactService contactService
    MockHttpServletRequest request

    def setup() {
        User adminUser = new User(firstName: 'admin', lastName: 'admin', username: 'admin', password: 'admin@123', email: 'admin@causecode.com')

        SpringSecurityService service = new SpringSecurityService()
        service.metaClass.currentUser = adminUser
        service.metaClass.encodePassword = { String password -> 'ENCODED_PASSWORD' }
        adminUser.springSecurityService = service

        assert adminUser.save(flush: true)

        contactService = new ContactService()
        contactService.springSecurityService = service

        request = new MockHttpServletRequest()
        List<Locale> localeList = new ArrayList<>()
        localeList.add(new Locale('India', 'en_IN'))
        request.setPreferredLocales(localeList)
    }

    void 'test resolve parameters method'() {
        given: 'Map containing various key-value pairs for contact'
        Map args = [city : 'Pune', state: 'Maharashtra', cityState : 'Pune,Maharashtra', cityStateCountry: 'Pune,Maharashtra,India',
                countryId : 1, email : 'admin@causecode.com', altEmail: 'admin@gmail.com', mobileCountryCode : '91', phoneNumber: '9876543210'
        ]

        when: 'resolveParameters is called'
        boolean result = contactService.resolveParameters(args, request, 'contact')

        then: 'resolveParameters must return true and arg map must satisfy conditions for given values'
        result == true
        args['contact.email'] == 'admin@causecode.com'
        args['contact.altEmail'] == 'admin@gmail.com'
        args['contact.address.latitude'] == 0
        args['contact.address.longitude'] == 0
        args['contact.address.city.id'] == 1
    }

    void 'test resolve parameters method when mobileCountryCode,phoneNumber is not provided'() {
        given: 'Map containing various key-value pairs'
        Map args = [city : 'Pune', state: 'Maharashtra', cityStateCountry : 'Pune,India',
                longitude : '73°51`19` E', latitude: '18°31`10` N'
        ]

        when: 'resolveParameters is called'
        boolean result = contactService.resolveParameters(args, request, 'contact')

        then: 'resolveParameters must return true and arg map must satisfy conditions for given values'
        result == true
        args['contact.address.longitude'] == '73°51`19` E'
        args['contact.address.latitude'] == '18°31`10` N'
        args['contact.address.city.id'] == 1
    }

    void 'test resolve parameters method when country field is missing'() {
        given: 'Map containing various key-value pairs'
        Map args = [city : 'Pune', state: 'Maharashtra', cityState : 'Pune,Maharashtra', country : null, countryId : 1,
                cityState : 'Pune,Maharashtra', zip: '366366', mobileCountryCode: '91', phoneNumber: '9876543210',
                facebook: 'https://www.facebook.com/causecode', twitter : '@causecode'
        ]

        Country.metaClass.'static'.get = { Long id ->
            return new Country(name: 'India', code: 'IND')
        }

        when: 'resolveParameters is called'
        boolean result = contactService.resolveParameters(args, request, 'contact')

        then: 'resolveParameters must return true'
        result == true
        args['contact.twitter'] == '@causecode'
        args['contact.facebook'] == 'https://www.facebook.com/causecode'

        when: 'country id and country both fields are missing'
        result = contactService.resolveParameters(args, request, 'contact')

        then: 'resolveParameters must return true'
        result == true
        args['contact.twitter'] == '@causecode'
        args['contact.facebook'] == 'https://www.facebook.com/causecode'
    }

    void 'test hasErrors method when instance does not have any errors'() {
        given: 'Map containing various key-value pairs'
        Country india = new Country(name: 'India', code: 'IND')
        City pune = new City(country: india, city: 'Pune', state: 'Maharashtra', stateCode: 'MH')
        Location location = new Location(city: pune, address: 'Baner', zip: '377373', name: 'causecode')
        Contact contact = new Contact(address: location)
        Map args = [contact: contact]

        when: 'hasErrors is called'
        boolean result = contactService.hasErrors(args, 'contact')

        then: 'hasErrors will return false'
        result == false
    }

    void "test hasErrors method when instance has errors"() {
        given: 'Map containing various key-value pairs'
        Country india = new Country(code: 'IND')
        PhoneCountryCode indiaCode = new PhoneCountryCode(code: '91',country: india)
        Phone phone = new Phone(countryCode: indiaCode)
        City pune = new City(state: 'Maharashtra', stateCode: 'MH')
        Location location = new Location(city: pune)
        Contact contact = new Contact(phone: phone, address: location)
        Map args = [contact: contact]

        when: 'hasErrors is called'
        boolean result = contactService.hasErrors(args, 'contact')

        then: 'hasErrors will return true'
        result == true
    }
}
