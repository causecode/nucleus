/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.contact

import com.cc.geo.location.Country

class PhoneCountryCode {

    Date dateCreated
    Date lastUpdated

    String code
    Country country

    static constraints = {
        code blank: false, unique: true
        dateCreated bindable: false
        lastUpdated bindable: false
    }

    static mapping = {
        table "cc_location_phone_country_code"
    }

}
