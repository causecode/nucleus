/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.contact

import javax.servlet.http.HttpServletRequest

import com.cc.geo.location.City
import com.cc.geo.location.Country
import com.cc.user.User

class ContactService {

    def springSecurityService

    boolean resolveParameters(Map args, HttpServletRequest request, String fieldName = "contact") {
        Country countryInstance
        String city = args["city"]
        String state = args["state"]
        String country = args["country"]
        PhoneCountryCode countryCodeInstance

        User currentUserInstance = springSecurityService.currentUser
        String logText = "User [${currentUserInstance?.email ?: 'Anonymous'}]"

        if(args["cityState"]) {
            List cityState = args["cityState"].tokenize(',')
            try {
                city = cityState[0]?.trim()
                state = cityState[1]?.trim()
            } catch(ArrayIndexOutOfBoundsException e) {
            }
        }
        if(args["cityStateCountry"]) {
            List cityStateCountry = args["cityStateCountry"].tokenize(',')
            try {
                city = cityStateCountry[0]?.trim()
                if (cityStateCountry.size() < 3) {
                    state = ""
                    country = cityStateCountry[1]?.trim()
                } else {
                    state = cityStateCountry[1]?.trim()
                    country = cityStateCountry[2]?.trim()
                }
            } catch(ArrayIndexOutOfBoundsException e) {
            }
        }
        if(!args["countryId"] && !country) {
            log.warn "$logText no country found in params. Setting [${request.locale.displayCountry}]"
            country = request.locale.displayCountry
        }
        if(country) {
            countryInstance = Country.findOrSaveByName(country)
        } else if(args["countryId"]) {
            countryInstance = Country.get(args["countryId"].toLong()) // Useful in token auto-complete
        }
        log.info "Resolved [city: $city, state: $state, country: $country]"

        City cityInstance = City.findOrSaveWhere(city: city, state: state, country: countryInstance)
        if(cityInstance.hasErrors()) {
            log.warn "Error saving city instance: $cityInstance.errors"
        }
        if(args["mobileCountryCode"]) {
            countryCodeInstance = PhoneCountryCode.findOrSaveWhere(code: args["mobileCountryCode"], country: countryInstance)
            args["${fieldName}.phone.countryCode.id"] = countryCodeInstance.id
        }
        if(args["phoneNumber"]) {
            args["${fieldName}.phone.number"] = args["phoneNumber"]
        }
        args["${fieldName}.email"] = args["email"]
        args["${fieldName}.altEmail"] = args["altEmail"]
        args["${fieldName}.facebook"] = args["facebook"]
        args["${fieldName}.linkedIn"] = args["linkedIn"]
        args["${fieldName}.twitter"] = args["twitter"]
        args["${fieldName}.address.zip"] = args["zip"]
        args["${fieldName}.address.latitude"] = args["latitude"] ?: 0
        args["${fieldName}.address.longitude"] = args["longitude"] ?: 0
        args["${fieldName}.address.city.id"] = cityInstance.id
        return true
    }

    boolean hasErrors(def instance, String fieldName = "contact") {
        List validationResult = []
        validationResult << instance[fieldName]?.validate()
        validationResult << instance[fieldName]?.phone?.validate()
        validationResult << instance[fieldName]?.address?.validate()
        validationResult << instance[fieldName]?.address?.city?.validate()
        validationResult << instance[fieldName]?.address?.city?.country?.validate()

        def currentUserInstance = springSecurityService.currentUser
        String logText = "User [${currentUserInstance?.email ?: 'Anonymous'}]"

        if(validationResult.contains(false)) {
            log.warn instance[fieldName]?.errors
            log.warn instance[fieldName]?.phone?.errors
            log.warn instance[fieldName]?.address?.errors
            log.warn instance[fieldName]?.address?.city?.errors
            log.warn instance[fieldName]?.address?.city?.country?.errors
            return true
        }
        return false
    }

}