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

    def init = { servletContext ->
        log.debug "Nucleus Bootstrap started executing .."

        NucleusUtils.initialize()

        XML.registerObjectMarshaller(new SitemapMarshaller())

        JSON.registerObjectMarshaller(new CustomValidationErrorMarshaller(grailsApplication.mainContext))

        log.debug "Nucleus Bootstrap finished executing."
    }

}