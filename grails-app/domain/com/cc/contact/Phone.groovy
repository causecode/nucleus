/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.contact


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