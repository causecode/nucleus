package com.causecode.util

import com.causecode.currency.Currency
import com.causecode.exceptions.DBTypeNotFoundException
import com.causecode.exceptions.MissingConfigException
import grails.plugins.mail.MailService
import grails.test.mixin.Mock
import grails.test.mixin.TestMixin

import grails.test.mixin.support.GrailsUnitTestMixin
import grails.test.runtime.FreshRuntime
import grails.util.Holders
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import spock.lang.Specification
import spock.lang.Unroll

/**
 * This class specifies unit test cases for {@link com.causecode.util.NucleusUtils}
 */
@TestMixin(GrailsUnitTestMixin)
@Mock([Currency, MailService])
class NucleusUtilsSpec extends Specification {

    void 'test getAppName method'() {
        when: 'getAppName() is called'
        Holders.config.setAt('app.name', 'nucleus')
        String appName = NucleusUtils.appName

        then: 'result must match with provided string'
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
        object != null
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

    @Unroll
    void "test method getDBType with valid params"() {
        when: 'getDBTYpe method is hit and below params are passed'
        Holders.config.dataSource.driverClassName = mysqlDriver
        Holders.config.dataSource.url = mysqlUrl
        Holders.config.grails.mongodb.databaseName = mongoDBName
        Holders.config.grails.mongodb.host = mongoHost
        DBTypes result = NucleusUtils.DBType

        then: 'metod responds with valid databse name'
        result == database

        where:
        mysqlDriver   |  mysqlUrl     |  mongoDBName     |  mongoHost    | database
        'com.mysql.'  | 'jdbc:mysql:' |  null            |  null         | DBTypes.MYSQL
        ''            | ''            | 'test_mongo'     | 'localhost'   | DBTypes.MONGO
    }

    void "test method getDBType with invalid params"() {
        when: 'getDBTYpe method is hit and below params are passed'
        Holders.config.dataSource.driverClassName = mysqlDriver
        Holders.config.dataSource.url = mysqlUrl
        Holders.config.grails.mongodb.databaseName = mongoDBName
        Holders.config.grails.mongodb.host = mongoHost
        NucleusUtils.DBType

        then: 'DBTypeNotFound exception is thrown with valid error message'
        DBTypeNotFoundException ex = thrown()
        ex.message == 'Could not infer dbType from application config.'

        where:
        mysqlDriver  | mysqlUrl      | mongoDBName  | mongoHost
        'com.mysql.' | 'jdbc:mysql:' | 'test_mongo' | 'localhost'
        ''           | ''            | ''           | ''
    }

    @FreshRuntime
    void "test getDbType method when no database configuration is present in the installing app"() {
        when: 'getDBType method is hit and database has not been configured in installing application'
        Holders.config.dataSource = [:]
        Holders.config.grails.mongodb = [:]
        NucleusUtils.DBType

        then: 'Method throws MissingConfigException exception'
        MissingConfigException e = thrown()
        e.message == 'Database configuration missing from Application config.'
    }
}
