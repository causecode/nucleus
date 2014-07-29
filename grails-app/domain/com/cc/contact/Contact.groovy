/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.contact

import com.cc.geo.location.Location

/**
 * Used to store location and contact information.
 *
 */
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