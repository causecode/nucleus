/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.geo.location

import com.cc.geo.location.City

class Location {

    Date dateCreated
    Date lastUpdated

    String name
    String address
    String zip

    City city

    float latitude
    float longitude

    static constraints = {
        address nullable: true
        dateCreated bindable: false
        lastUpdated bindable: false
        name nullable: true
    }

    def getFullAddress() {
        return address + ', ' + city.cityStateCountry + ' - ' + zip
    }
}