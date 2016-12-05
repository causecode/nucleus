/*
 * Copyright (c) 2016, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.contact

import com.causecode.geo.location.Country
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Used to store code and country information.
 *
 */
@ToString(includes = ['id'], includePackage = false)
@EqualsAndHashCode
class PhoneCountryCode {

    Date dateCreated
    Date lastUpdated

    String code
    Country country

    static constraints = {
        code blank: false, unique: true, maxSize: 2
        dateCreated bindable: false
        lastUpdated bindable: false
    }
}
