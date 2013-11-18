/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.geo.location

//import com.lucastex.grails.fileuploader.UFile

class Country {

    String code
    String name

    //UFile flag

    Date dateCreated
    Date lastUpdated

    static constraints = {
        code nullable: true
        name blank: false, unique: true
        //flag nullable: true
        dateCreated bindable: false
        lastUpdated bindable: false
    }

    static mapping = {
        table "cc_location_country"
    }

    @Override
    String toString() {
        name
    }

}
