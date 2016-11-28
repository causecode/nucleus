/*
 * Copyright (c) 2016, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.contact

import com.causecode.geo.location.Location
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Used to store location and contact information.
 *
 */
@ToString(includes = ['id'], includePackage = false)
@EqualsAndHashCode
class Contact {

    Date dateCreated
    Date lastUpdated

    Location address

    String email
    String altEmail
    String facebook
    String twitter
    String linkedIn

    Phone phone

    static constraints = {
        altEmail email: true, nullable: true
        dateCreated bindable: false
        email email: true, nullable: true
        facebook nullable: true, url: true
        twitter nullable: true
        lastUpdated bindable: false
        linkedIn nullable: true
        phone nullable: true
    }

    static mapping = {
        address cascade: 'all'
        phone cascade: 'all'
    }
}
