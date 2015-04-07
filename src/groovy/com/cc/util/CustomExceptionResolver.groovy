/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.util

import grails.util.Environment

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.web.errors.GrailsExceptionResolver
import org.springframework.web.servlet.ModelAndView

import com.cc.user.User

class CustomExceptionResolver extends GrailsExceptionResolver {

    private static Log log = LogFactory.getLog(this)

    @Override
    ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        if (!Environment.isDevelopmentMode()) {
            return super.resolveException(request, response, handler, e)
        }

        def mailService = NucleusUtils.getMailService()

        if (!mailService) {
            log.info "No mail plugin installed to send exception message"
            return super.resolveException(request, response, handler, e)
        }

        StringWriter errors = new StringWriter()
        e.printStackTrace(new PrintWriter(errors))

        String appName = NucleusUtils.appName

        Map model = [exception: e, stackTrace: errors.toString(), appName: appName, environmentName: Environment.current.name]

        if (request) {
            String requestURL = request.forwardURI
            if (request.queryString) {
                requestURL += "?" + request.queryString
            }

            model.requestURL = requestURL

            def springSecurityService = NucleusUtils.getBean("springSecurityService")
            User currentUserInstance = springSecurityService.getCurrentUser()

            model.userInstance = currentUserInstance
        }

        NucleusUtils.sendExceptionEmail(e, model)

        return super.resolveException(request, response, handler, e)
    }
}