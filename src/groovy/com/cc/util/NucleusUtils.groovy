/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.util

import grails.util.Holders

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * A utility class to provide some common & useful stuff
 * @author Shashank Agrawal
 * @since v0.3.1
 */
class NucleusUtils {

    private static Log logger = LogFactory.getLog(this)

    static Object mailService

    static String getAppName() {
        Holders.getGrailsApplication().metadata["app.name"].capitalize()
    }

    static Object getBean(String serviceName) {
        Holders.getApplicationContext()[serviceName]
    }

    static void getMailService() {
        mailService
    }

    static void initialize() {
        logger.debug "Initilizing NucleusUtil.."

        if (Holders.getPluginManager().hasGrailsPlugin("asynchronousMail")) {
            mailService = getBean("asynchronousMailService")
        } else if (Holders.getPluginManager().hasGrailsPlugin("mail")) {
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

    /**
     * Method used to send email on exception to configured email or default to developers@causecode.com
     * with detailed stacktrace and error line number.
     * @param exceptions A list of exceptions
     * @param model An map containing all parameters to send email.
     */
    static void sendExceptionEmail(Exception e, Map model) {
        logger.debug "Sending exception email"

        String messageBody = getBean("groovyPageRenderer").render([template: "/email-templates/error",
            plugin: "nucleus", model: model])

        String messageSubject = "[$appName][Envirnoment.current] Internal Server Error occurred."

        String toEmail = Holders.getFlatConfig()["app.technical.support.email"] ?: "developers@causecode.com"

        mailService.sendMail {
            to toEmail
            from "bootstrap@causecode.com"
            subject messageSubject
            html messageBody
        }
        logger.debug "Sent Email on exception successfully."
    }
}