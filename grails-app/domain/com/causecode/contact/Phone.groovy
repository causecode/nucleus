package com.causecode.contact

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Used to store phone number and phone country code information.
 *
 */
@ToString(includes = ['id'], includePackage = false)
@EqualsAndHashCode
class Phone {

    String number

    PhoneCountryCode countryCode

    Date dateCreated
    Date lastUpdated

    static constraints = {
        number size: 10..10
        dateCreated bindable: false
        lastUpdated bindable: false
    }

    def getFullPhoneNumber() {
        return '+(' + countryCode.code + ')' + number
    }
}
