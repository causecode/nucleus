package com.causecode.geo.location

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Used to store country code and name information.
 *
 */
@ToString(includes = ['name'], includePackage = false)
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
}
