/*
 * Copyright (c) 2011-Present CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.util

import com.causecode.currency.Currency
import grails.gsp.PageRenderer
import grails.plugins.mail.MailService
import grails.test.mixin.Mock
import grails.test.mixin.TestMixin

import grails.test.mixin.support.GrailsUnitTestMixin
import grails.util.Holders
import org.apache.commons.logging.Log
import org.grails.gsp.GroovyPagesTemplateEngine
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import spock.lang.Specification

@TestMixin(GrailsUnitTestMixin)
@Mock([Currency, MailService])
class NucleusUtilsSpec extends Specification {

    void 'test getAppName method'() {
        when: 'getAppName() is called'
        Holders.config.setAt('app.name', 'nucleus')
        String appName = NucleusUtils.getAppName()

        then:
        appName == 'Nucleus'
    }

    void 'test initialize method'() {
        given: 'applicationContext'
        ApplicationContext applicationContext = Holders.applicationContext

        when: 'initialize method is called'
        NucleusUtils.initialize(applicationContext)

        then: 'No exception is thrown'
        notThrown(BeansException)
    }

    void 'test getBean method'() {
        when: 'getBean method is called'
        Object object = NucleusUtils.getBean('mailService')

        then: 'bean object is returned'
        object!=null
    }

    void 'test save method'() {
        when: 'save method is called for given valid currency instance'
        Currency currency = new Currency(code: 'INR', name: 'Indian Rupees')
        boolean result = NucleusUtils.save(currency, true)

        then: 'save method must return true'
        result == true

        when: 'save method is called for given invalid currency instance'
        currency = new Currency()
        result = NucleusUtils.save(currency, true)

        then: 'save method must return false'
        result == false

        when: 'save method is called for given null currency instance'
        currency = null
        result = NucleusUtils.save(currency, true)

        then: 'save method must return false'
        result == false
    }

    void 'test sendExceptionEmail method'() {
        given: 'List of exceptions to be sent in email'

        Holders.config.setAt('app.name', 'nucleus')
        Map map = [:]

        PageRenderer groovyPageRenderer = new PageRenderer(new GroovyPagesTemplateEngine())

        NucleusUtils.metaClass.'static'.getBean = { String serviceName ->
            return groovyPageRenderer
        }

        groovyPageRenderer.metaClass.render = {
            Map param -> return 'Error occurred'
        }

        String logStatement
        NucleusUtils.logger = [debug: { Object message ->
            logStatement = message
        }, info: { Object message ->
            logStatement = message
        }, error: { Object message, Throwable e = new Exception() ->
            logStatement = message
            println e.message
        } ] as Log

        NucleusUtils.mailService = null
        when: 'sendExceptionEmail method is called and mailService is null'

        NucleusUtils.sendExceptionEmail(new StackOverflowError(), map)

        then: 'mail cannot be sent and method returns'
        logStatement == 'Could not send email as MailService bean is null.'

        when: 'sendExceptionEmail method is called'
        NucleusUtils.mailService = [sendMail: { Closure callable ->
            return
        }] as MailService

        NucleusUtils.sendExceptionEmail(new StackOverflowError(), map)

        then: 'mail is sent'
        logStatement == 'Exception email sent'
    }
}
