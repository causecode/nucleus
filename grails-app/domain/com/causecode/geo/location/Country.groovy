/*
 * Copyright (c) 2016, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.geo.location

import groovy.transform.EqualsAndHashCode

/**
 * Used to store country code and name information.
 *
 */
@EqualsAndHashCode
class Country {

    String code
    String name

    // TODO CDN Google inject issue on build
    // UFile flag

    Date dateCreated
    Date lastUpdated

    static constraints = {
        code nullable: true
        dateCreated bindable: false
        //flag nullable: true
        lastUpdated bindable: false
        name blank: false, unique: true
    }

    @Override
    String toString() {
        name
    }

}
