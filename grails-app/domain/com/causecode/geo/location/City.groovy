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
 * Used to store city, state and stateCode information.
 *
 */
@EqualsAndHashCode
@SuppressWarnings(['GrailsDomainReservedSqlKeywordName'])
class City {

    Date dateCreated
    Date lastUpdated

    String city
    String state
    String stateCode

    Country country

    static constraints = {
        city nullable: true
        dateCreated bindable: false
        lastUpdated bindable: false
        state nullable: true
        stateCode nullable: true
    }

    @Deprecated
    String getCityStateCountry() {
        def cityList = []
        cityList.add(city)
        state ? cityList.add(state) : void
        cityList.add(country.name)
        return cityList.join(', ')
    }

    String toString(List fields) {
        List validFields = []
        fields.each {
            if (this[it]) {
                validFields << this[it]
            }
        }
        return validFields.join(', ')
    }

    @Override
    String toString() {
        return "City [$id]"
    }
}
