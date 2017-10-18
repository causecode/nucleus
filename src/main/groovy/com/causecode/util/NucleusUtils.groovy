/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.util

import com.causecode.exceptions.MissingConfigException
import grails.util.Environment
import grails.util.Holders
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import com.causecode.exceptions.DBTypeNotFoundException

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
        logger.debug 'Initializing NucleusUtil..'

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

    static void sendExceptionEmail(Throwable exception, Map model) {
        sendExceptionEmail([exception], model)
    }

    /**
     * A utility method which infers database name from config properties.
     *
     * @return com.causecode.util.DBTypes
     * @throws DBTypeNotFoundException when no name is inferred or both the names are inferred from the config
     * properties.
     * @throws MissingConfigException when database configuration is missing in installing application.
     */
    static DBTypes getDBType() throws DBTypeNotFoundException, MissingConfigException {
        Map mysqlDB = Holders.config.dataSource ?: [:]
        Map mongoDB = Holders.config.grails.mongodb ?: [:]

        logger.debug('NucleusUtils.getDBType()...')

        if (!mysqlDB && !mongoDB) {
            throw new MissingConfigException('Database configuration missing from Application config.')
        }

        if (mysqlDB.driverClassName?.contains('mysql') && mysqlDB.url?.contains('mysql')
                    && !mongoDB.databaseName && !mongoDB.host ) {
                return DBTypes.MYSQL
        } else if (mongoDB.databaseName && mongoDB.host && !mysqlDB.driverClassName && !mysqlDB.url) {
            return DBTypes.MONGO
        }

        throw new DBTypeNotFoundException('Could not infer dbType from application config.')
    }
}
