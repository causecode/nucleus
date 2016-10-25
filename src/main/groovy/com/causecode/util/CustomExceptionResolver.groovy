/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.util

import grails.util.Environment

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.grails.web.errors.GrailsExceptionResolver
import org.springframework.web.servlet.ModelAndView

/**
 * Used to handle any exception thrown while processing any request and
 * sends an exception email to configured email for non development environment.
 * @author Shashank Agrawal
 * @since 0.3.3
 */
class CustomExceptionResolver extends GrailsExceptionResolver {

    @Override
    ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                  Object handler, Exception e) {
        if (Environment.isDevelopmentMode() || !NucleusUtils.mailService) {
            return super.resolveException(request, response, handler, e)
        }

        Map model = [:]

        String requestURL = request.forwardURI
        if (request.queryString) {
            requestURL += '?' + request.queryString
        }

        model['requestURL'] = requestURL
        // Some angular based client side app may send header for current URL
        model['angularURL'] = request.getHeader('angular-url')

        def springSecurityService = NucleusUtils.getBean('springSecurityService')
        if (springSecurityService) {
            model['userInstance'] = springSecurityService.currentUser
        }

        NucleusUtils.sendExceptionEmail(findWrappedException(e), model)

        return super.resolveException(request, response, handler, e)
    }
}
