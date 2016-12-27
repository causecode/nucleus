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

    private static final Log logger = LogFactory.getLog(this)
    static Object mailService

    static String getAppName() {
        Holders.config.'app.name'.capitalize()
    }

    static Object getBean(String serviceName) {
        Holders.applicationContext[serviceName]
    }

    static void initialize(ApplicationContext applicationContext) {
        logger.debug 'Initilizing NucleusUtil..'

        try {
            mailService = applicationContext.getBean('asynchronousMailService')
        } catch (BeansException e) {
            logger.debug 'AsynchronousMailService bean not found, trying to inject MailService.'
            try {
                mailService = applicationContext.getBean('mailService')
            } catch (BeansException ex) {
                logger.debug 'MailService bean not found.'
            }
        }

        logger.debug 'NucleusUtil initialized.'
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
     * @param exceptions A list of exceptions
     * @param model OPTIONAL A map containing all parameters to send email.
     * @param model.userInstance OPTIONAL Instance of {@link com.causecode.user.User User} who was logged in
     * @param model.requestURL OPTIONAL Grails server URL where exception occurred
     * @param model.angularURL OPTIONAL Client side angular app URL
     * @param model.codeExceutionFor OPTIONAL Any string to tell where exception occurred like "processing all users"
     * @since 0.3.3
     */
    static void sendExceptionEmail(List<Throwable> exceptions, Map model) {
        logger.debug 'Sending exception email'
        Map tempModel = model
        tempModel = model ?: [:]
        tempModel['appName'] = appName
        tempModel['exceptions'] = exceptions

        String messageBody = getBean('groovyPageRenderer').render([template: '/email-templates/error',
                plugin: 'nucleus', model: tempModel])

        String messageSubject = "[$appName][${Environment.current.name}] Internal Server Error"

        String toEmail = Holders.config['app.technical.support.email'] ?: 'developers@causecode.com'

        if (!mailService) {
            logger.debug 'Could not send email as MailService bean is null.'
            return
        }

        mailService.sendMail {
            to(toEmail)
            from(tempModel['from'] ?: 'bootstrap@causecode.com')
            subject messageSubject
            html messageBody
        }

        logger.debug 'Exception email sent'
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
     *         grails.config.locations = ['path/to/application-production-local.groovy']
     *     }
     *     development {
     *         grails.config.locations = ['path/to/application-development-local1.groovy',
     *                  'path/to/application-development-local2.groovy']
     *     }
     * }
     *
     * note: config override depends on order of passed file locations.
     *
     * @params application
     * @params org.springframework.core.env.Environment environment
     *
     * @since 0.4.2
     * @author Ankit Agrawal
     */
    // TODO Completely automate this process in future.
    @SuppressWarnings(['JavaIoPackageAccess'])
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

    /**
     * This method is used to merge configurations defined at application.groovy file of parent app and configurations
     * from DefaultConfig file of any plugin.
     * The order of merge is important.
     * We started with configurations from DefaultConfig.groovy and then merged the configurations
     * defined in application.groovy file of parent app.
     * This lets the configurations from DefaultConfig act as default values but lets the user-supplied
     * values in the application.groovy file of parent app override them.
     *
     * @params className
     *
     * @return merged ConfigObject
     */
    static ConfigObject getMergedConfigurations(String className) {
        ConfigObject applicationConfiguration = new ConfigSlurper(Environment.current.name).parse(new
                    GroovyClassLoader(this.classLoader).loadClass('application'))

        ConfigObject pluginConfiguration = new ConfigSlurper(Environment.current.name).parse(new
                GroovyClassLoader(this.classLoader).loadClass(className))

        return (pluginConfiguration ?: new ConfigObject()).merge(applicationConfiguration ?: new ConfigObject())
    }
}
