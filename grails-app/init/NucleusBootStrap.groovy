/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

import com.causecode.util.StringAsGspRenderer
import grails.converters.JSON
import grails.converters.XML

import com.causecode.util.CustomValidationErrorMarshaller
import com.causecode.util.SitemapMarshaller
import grails.core.GrailsApplication

/**
 * Configuration class for registering marshallers and registering metaMethods.
 */
class NucleusBootStrap {

    GrailsApplication grailsApplication
    StringAsGspRenderer stringAsGspRenderer

    def init = { servletContext ->
        log.debug 'Nucleus Bootstrap started executing ..'

        registerStringUtilityMethods()

        XML.registerObjectMarshaller(new SitemapMarshaller())

        JSON.registerObjectMarshaller(new CustomValidationErrorMarshaller(grailsApplication.mainContext))

        log.debug 'Nucleus Bootstrap finished executing.'
    }

    private static void registerStringUtilityMethods() {
        /**
         * Used to convert camel case to title or natural case.
         * @example "firstName".camelCaseToTitle() == "First Name"
         */
        String.metaClass.camelCaseToTitle = {
            String title = delegate.replaceAll(/\B[A-Z]/) { ' ' + it }.capitalize()
            return title
        }

        /**
         * Used to convert hyphenated string to camel case.
         * @Example "some-name".toUpperCamelCase() == "someName"
         */
        String.metaClass.toUpperCamelCase = {
            Object[] value = value[2].toUpperCase()
            return delegate.replaceAll('(-)([A-Za-z0-9])', value)
        }

        /**
         * Used to convert camel case string to hyphenated/snaked case.
         * @Example "someName".toSnakeCase() == "some-name"
         */
        String.metaClass.toSnakeCase = {
            return delegate.replaceAll(/([A-Z])/, /-$1/).toLowerCase().replaceAll(/^-/, '')
        }

        /**
         * Used to capitalize first character of each word in a string.
         * @Example "some word".capitalizeAll() == "Some Word"
         */
        String.metaClass.capitalizeAll = {
            return delegate.split(' ')*.capitalize().join(' ')
        }
    }
}
