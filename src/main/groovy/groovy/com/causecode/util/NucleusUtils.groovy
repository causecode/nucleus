/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.causecode.util

import grails.util.Environment
import grails.util.Holders
import org.springframework.context.ApplicationContext
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
        Holders.getConfig().app.name.capitalize()
        //Holders.getGrailsApplication().metadata["app.name"].capitalize()
    }

    static Object getBean(String serviceName) {
        Holders.getApplicationContext()[serviceName]
    }

    static void getMailService() {
        mailService
    }

    static void initialize(ApplicationContext applicationContext) {
        logger.debug "Initilizing NucleusUtil.."

        try {
            mailService = applicationContext.getBean('asynchronousMailService')
        } catch(Exception e) {
            println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> caught new exception"
            mailService = applicationContext.getBean('mailService')
        }

        if (!mailService) {
            println ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> mail service null"
            return
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
     * 
     * @param exceptions A list of exceptions
     * @param model OPTIONAL A map containing all parameters to send email.
     * @param model.userInstance OPTIONAL Instance of {@link com.causecode.user.User User} who was logged in
     * @param model.requestURL OPTIONAL Grails server URL where exception occurred
     * @param model.angularURL OPTIONAL Client side angular app URL
     * @param model.codeExceutionFor OPTIONAL Any string to tell where exception occurred like "processing all users"
     * 
     * @since 0.3.3
     */
    static void sendExceptionEmail(List<Throwable> exceptions, Map model) {
        logger.debug "Sending exception email"

        model = model ?: [:]
        model["appName"] = getAppName()
        model["exceptions"] = exceptions

        String messageBody = getBean("groovyPageRenderer").render([template: "/email-templates/error",
            plugin: "nucleus", model: model])

        String messageSubject = "[$appName][${Environment.current.name}] Internal Server Error"

        String toEmail = Holders.getFlatConfig()["app.technical.support.email"] ?: "developers@causecode.com"

        mailService.sendMail {
            to (toEmail)
            from (model["from"] ?: "bootstrap@causecode.com")
            subject messageSubject
            html messageBody
        }
        logger.debug "Exception email sent"
    }

    static void sendExceptionEmail(Throwable exception, Map model) {
        sendExceptionEmail([exception], model)
    }
}