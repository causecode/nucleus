/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.util

import grails.boot.config.GrailsAutoConfiguration
import grails.util.Environment
import org.springframework.core.env.Environment as SEnvironment
import grails.util.Holders
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.core.env.MapPropertySource

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
        } catch(BeansException e) {
            logger.debug "AsynchronousMailService bean not found, trying to inject MailService."
            try {
                mailService = applicationContext.getBean('mailService')
            } catch(BeansException ex) {
                logger.debug "MailService bean not found."
            }
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
    static boolean save(Object domainInstance, boolean flush, def log = logger) {
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

        if (!mailService) {
            logger.debug 'Could not send email as MailService bean is null.'
            return
        }

        mailService.sendMail {
            to (toEmail)
            from (model["from"] ?: "bootstrap@causecode.com")
            subject messageSubject
            html messageBody
        }

        logger.debug "Exception email sent"
    }

    /**
     * Method used to override configurations from an external file. This method can be called from any application that
     * wants to override it's application configuration properties using an external config file.
     * Location of external config files are picked from application.groovy file of parent application.
     * Locations are to be provided under 'grails.config.locations'
     *
     * example:
     * Environment specific config override file locations.
     * environments {
     *     production {
     *         grails.config.locations = ['path/to/application-local.groovy']
     *     }
     *     development {
     *         grails.config.locations = ['path/to/application-local.groovy']
     *     }
     *     test {
     *         grails.config.locations = ['path/to/application-local.groovy']
     *     }
     *     staging {
     *         grails.config.locations = ['path/to/application-local.groovy']
     *     }
     * }
     *
     * note: config override depends on order of passes file locations.
     *
     * @params application
     * @params org.springframework.core.env.Environment environment
     *
     * @since 0.4.1
     * @author Ankit Agrawal
     */
    static void addExternalConfig(GrailsAutoConfiguration application, SEnvironment environment) {
        URL applicationGroovy = application.getClass().classLoader.getResource('application.groovy')
        if (applicationGroovy) {
            ConfigObject applicationConfiguration = new ConfigSlurper(Environment.current.name).parse(applicationGroovy)

            (applicationConfiguration.grails.config.locations).each { String configurationResource ->
                ConfigObject config = new ConfigSlurper(Environment.current.name)
                        .parse(new File(configurationResource).toURI().toURL())
                environment.propertySources.addFirst(new MapPropertySource('external-config', config))
            }
        }
    }

    static void sendExceptionEmail(Throwable exception, Map model) {
        sendExceptionEmail([exception], model)
    }
}
