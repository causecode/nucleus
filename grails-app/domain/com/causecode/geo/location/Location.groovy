package com.causecode.geo.location

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Used to stores all location information.
 *
 */
@ToString(includes = ['id'], includePackage = false)
@EqualsAndHashCode
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
