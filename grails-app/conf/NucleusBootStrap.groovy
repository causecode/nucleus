/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

import grails.converters.JSON
import grails.converters.XML

import com.cc.util.CustomValidationErrorMarshaller
import com.cc.util.NucleusUtils
import com.cc.util.SitemapMarshaller

class NucleusBootStrap {

    def grailsApplication
    def stringAsGspRenderer

    def init = { servletContext ->
        log.debug "Nucleus Bootstrap started executing .."

        registerStringUtilityMethods()

        NucleusUtils.initialize()

        XML.registerObjectMarshaller(new SitemapMarshaller())

        JSON.registerObjectMarshaller(new CustomValidationErrorMarshaller(grailsApplication.mainContext))

        stringAsGspRenderer.cleanupTemplateCache()

        log.debug "Nucleus Bootstrap finished executing."
    }

    private static void registerStringUtilityMethods() {
        /**
         * Used to convert camel case to title or natural case.
         * @example "firstName".camelCaseToTile() == "First Name"
         */
        String.metaClass.camelCaseToTile = {
            String title = delegate.replaceAll(/\B[A-Z]/) { " " + it }.capitalize()
            return title
        }

        /**
         * Used to convert hyphenated string to camel case.
         * @Example "some-name".toUpperCamelCase() == "someName"
         */
        String.metaClass.toUpperCamelCase = {
            return delegate.replaceAll("(-)([A-Za-z0-9])", { Object[] value ->
                value[2].toUpperCase()
            })
        }

        /**
         * Used to convert camel case string to hyphenated/snaked case.
         * @Example "someName".toSnakeCase() == "some-name"
         */
        String.metaClass.toSnakeCase = {
            return delegate.replaceAll(/([A-Z])/, /-$1/).toLowerCase().replaceAll(/^-/, "")
        }

        /**
         * Used to capitalize first character of each word in a string.
         * @Example "some word".capitalizeAll() == "Some Word"
         */
        String.metaClass.capitalizeAll = {
            return delegate.split(" ").collect{ it.capitalize() }.join(" ")
        }
    }
}