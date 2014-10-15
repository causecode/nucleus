/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.util

import javax.servlet.ServletContext

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext
import org.codehaus.groovy.grails.plugins.DefaultGrailsPluginManager
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA

/**
 * A utility class to provide some common & usefull stuff
 * @author Shashank Agrawal
 * @since v0.3.1
 *
 */
class NucleusUtils {

    private static Log logger = LogFactory.getLog(this)

    static Object mailService

    static GrailsWebApplicationContext getApplicationContext() {
        servletContext.getAttribute(GA.APPLICATION_CONTEXT)
    }

    static String getAppName() {
        grailsApplication.metadata["app.name"].capitalize()
    }

    static Object getBean(String serviceName) {
        applicationContext[serviceName]
    }

    static DefaultGrailsApplication getGrailsApplication() {
        servletContext.getAttribute("grailsApplication")
    }

    static void getMailService() {
        mailService
    }

    static DefaultGrailsPluginManager getPluginManager() {
        servletContext.getAttribute(GA.PLUGIN_MANAGER)
    }

    static ServletContext getServletContext() {
        SCH.servletContext
    }

    static void initialize() {
        logger.debug "Initilizing NucleusUtil.."

        if (pluginManager.hasGrailsPlugin("asynchronousMail")) {
            mailService = getBean("asynchronousMailService")
        } else if (pluginManager.hasGrailsPlugin("mail")) {
            mailService = getBean("mailService")
        }

        logger.debug "NucleusUtil initialized."
    }

    /**
     * A utility method used to persist a domain instance, which first checks if domain instances
     * has any validation error or not and if it has validation error then it prints logs and simply
     * returns
     * @param domainInstance
     * @param flush
     * @param log An optional log instance of calling class to properly display log statements
     * @return
     */
    static boolean save(Object domainInstance, boolean flush, Log log = logger) {
        if (!domainInstance) {
            return false
        }

        domainInstance.validate()

        if (domainInstance.hasErrors()) {
            log.warn "Error saving $domainInstance $domainInstance.errors"
            return false
        }

        domainInstance.save(flush: flush)

        return true
    }
}