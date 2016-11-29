/*
 * Copyright (c) 2016, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.contact

import grails.plugin.springsecurity.SpringSecurityService

import javax.servlet.http.HttpServletRequest

import com.causecode.geo.location.City
import com.causecode.geo.location.Country

/**
 * ContactService resolves location and contact information parameters and validates instances.
 * @author Shashank Agrawal
 *
 */
class ContactService {

    /**
     * Dependency injection for the springSecurityService.
     */
    SpringSecurityService springSecurityService

    /**
     * Resolves location and contact information and save user instance with resolved parameter.
     * @param args list of location and contact parameters
     * @param request HttpServletRequest object used to get Locale for country parameter.
     * @return Boolean field specifying successfully saved current user instance with given parameters.
     */
    boolean resolveParameters(Map args, HttpServletRequest request, String fieldName = 'contact') {
        Country countryInstance
        String city = args['city']
        String state = args['state']
        String country = args['country']
        PhoneCountryCode countryCodeInstance
        String mCountryCode = 'mobileCountryCode'
        String phoneNumber = 'phoneNumber'

        Map locationMap = getCityStateCountry(args)
        city = locationMap.containsKey('city') ? locationMap['city'] : city
        state = locationMap.containsKey('state') ? locationMap['state'] : state
        country = locationMap.containsKey('country') ? locationMap['country'] : country
        countryInstance = retrieveCountryInstance(args, request, country)

        log.info "Resolved [city: $city, state: $state, country: $country]"

        City cityInstance = City.findOrSaveWhere(city: city, state: state, country: countryInstance)
        if (cityInstance.hasErrors()) {
            log.warn "Error saving city instance: $cityInstance.errors"
        }
        if (args[mCountryCode]) {
            countryCodeInstance = PhoneCountryCode.findOrSaveWhere(code: args[mCountryCode], country: countryInstance)
            args["${fieldName}.phone.countryCode.id"] = countryCodeInstance.id
        }
        if (args[phoneNumber]) {
            args["${fieldName}.phone.number"] = args[phoneNumber]
        }
        args["${fieldName}.email"] = args['email']
        args["${fieldName}.altEmail"] = args['altEmail']
        args["${fieldName}.facebook"] = args['facebook']
        args["${fieldName}.linkedIn"] = args['linkedIn']
        args["${fieldName}.twitter"] = args['twitter']
        args["${fieldName}.address.zip"] = args['zip']
        args["${fieldName}.address.latitude"] = args['latitude'] ?: 0
        args["${fieldName}.address.longitude"] = args['longitude'] ?: 0
        args["${fieldName}.address.city.id"] = cityInstance.id
        return true
    }

    /**
     * Retrieves city, state and country values from args map.
     * @param args list of location and contact parameters
     * @return Map containing corresponding values of city, state and country
     */
    private Map getCityStateCountry(Map args) {
        Map cityStateCountryMap = [:]
        int two = 2
        String comma = ','
        String strCityState = 'cityState'
        String strCityStateCountry = 'cityStateCountry'
        if (args[strCityState]) {
            List cityState = args[strCityState].tokenize(comma)
            if (cityState != null && cityState.size() >= two) {
                cityStateCountryMap['city'] = cityState[0]?.trim()
                cityStateCountryMap['state'] = cityState[1]?.trim()
            }
        }

        if (args[strCityStateCountry]) {
            List cityStateCountry = args[strCityStateCountry].tokenize(comma)
            if (cityStateCountry != null && cityStateCountry.size() > 0) {
                cityStateCountryMap['city'] = cityStateCountry[0]?.trim()
                if (cityStateCountry.size() < 3) {
                    cityStateCountryMap['state'] = ''
                    cityStateCountryMap['country'] = cityStateCountry[1]?.trim()
                } else {
                    cityStateCountryMap['state'] = cityStateCountry[1]?.trim()
                    cityStateCountryMap['country'] = cityStateCountry[two]?.trim()
                }
            }
        }
        return cityStateCountryMap
    }

    /**
     * Returns the country instance based on the parameters provided.
     * @param args list of location and contact parameters
     * @param request HttpServletRequest object used to get Locale for country parameter.
     * @param country country name
     * @return countryInstance
     */
    private Country retrieveCountryInstance(Map args, HttpServletRequest request, String country) {
        Country countryInstance
        String tempCountry = country
        String strCountryId = 'countryId'
        if (!args[strCountryId] && !tempCountry) {
            log.warn "User [${springSecurityService.currentUser?.email ?: 'Anonymous'}] no country found in params." +
                    "Setting [${request.locale.displayCountry}]"
            tempCountry = request.locale.displayCountry
        }
        if (tempCountry) {
            countryInstance = Country.findOrSaveByName(tempCountry)
        } else {
            if (args[strCountryId]) {
                countryInstance = Country.get(args[strCountryId].toLong()) // Useful in token auto-complete
            }
        }
        return countryInstance
    }

    /**
     * Checks whether instance validated without any errors or not for given field name.
     * @param instance Object to be validated
     * @param fieldName Name of embedded field.
     * @return True when instance for given field validated with no errors. Returns False if it throws errors in
     * validation.
     */
    boolean hasErrors(def instance, String fieldName = 'contact') {
        List validationResult = []
        validationResult << instance[fieldName]?.validate()
        validationResult << instance[fieldName]?.phone?.validate()
        validationResult << instance[fieldName]?.address?.validate()
        validationResult << instance[fieldName]?.address?.city?.validate()
        validationResult << instance[fieldName]?.address?.city?.country?.validate()

        def currentUserInstance = springSecurityService.currentUser
        log.info "User [${currentUserInstance?.email ?: 'Anonymous'}]"
        return validateResults(instance, validationResult, fieldName)
    }

    /**
     * Method simply checks whether there are any errors in data,
     * @param validationResult - List containing result of performed validations
     * @return boolean if there are errors, it returns true, otherwise false
     */
    boolean validateResults(def instance, List validationResult, String fieldName) {
        if (validationResult.contains(false)) {
            log.warn "Instance with field name $fieldName has errors"
            log.warn "$instance[fieldName]?.errors"
            log.warn "$instance[fieldName]?.phone?.errors"
            log.warn "$instance[fieldName]?.address?.errors"
            log.warn "$instance[fieldName]?.address?.city?.errors"
            return true
        }
        return false
    }
}
