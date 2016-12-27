/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.util

import grails.boot.config.GrailsAutoConfiguration
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.test.runtime.DirtiesRuntime
import grails.util.Environment
import org.springframework.core.env.MapPropertySource
import org.springframework.web.context.support.StandardServletEnvironment
import spock.lang.Specification

@TestMixin(GrailsUnitTestMixin)
class NucleusUtilsSpec extends Specification {

    @DirtiesRuntime
    void "test addExternalConfig method for successfully overriding configurations"() {
        given: "File instances with configurations and other required instances"
        File externalConfigFile =  new File('test-config.groovy')
        externalConfigFile.createNewFile()
        externalConfigFile << "grails.mail.overrideAddress = 'config.test@causecode.com'\n" +
                "grails.override.name = 'test-config.groovy'"

        File applicationConfigFile = new File('application-test.groovy')
        applicationConfigFile.createNewFile()
        applicationConfigFile << "grails.mail.overrideAddress = 'unit.test@causecode.com'\n" +
                "grails.original.appName = 'nucleus'\n" +
                "test { \n grails.config.locations = ['${externalConfigFile.absolutePath}'] \n } \n" +
                "development { \n grails.config.locations = ['${externalConfigFile.absolutePath}'] \n }"

        GrailsAutoConfiguration application = new GrailsAutoConfiguration()
        StandardServletEnvironment environment = new StandardServletEnvironment()

        // Adding application configurations in environment.
        ConfigObject config = new ConfigSlurper(Environment.current.name)
                .parse(new File(applicationConfigFile.absolutePath).toURI().toURL())
        environment.propertySources.addFirst(new MapPropertySource('app-config', config))
        assert environment.propertySources.propertySourceList[0].source.grails.mail.overrideAddress ==
                'unit.test@causecode.com'
        assert environment.propertySources.propertySourceList[0].source.grails.original.appName == 'nucleus'
        assert environment.propertySources.propertySourceList[0].source.grails.config.locations ==
                [externalConfigFile.absolutePath]

        and: "Mocked method"
        ClassLoader.metaClass.getResource = { String name ->
            return applicationConfigFile.toURI().toURL()
        }

        when: "addExternalConfig method is called"
        NucleusUtils.addExternalConfig(application, environment)

        then: "propertis are read from given external file and are added to environment's propertySource"
        // Old properties
        environment.propertySources.propertySourceList[1].source.grails.mail.overrideAddress ==
                'unit.test@causecode.com'
        environment.propertySources.propertySourceList[1].source.grails.original.appName == 'nucleus'
        environment.propertySources.propertySourceList[1].source.grails.config.locations ==
                [externalConfigFile.absolutePath]

        // New properties added from external config file.
        environment.propertySources.propertySourceList[0].source.grails.mail.overrideAddress ==
                'config.test@causecode.com'
        environment.propertySources.propertySourceList[0].source.grails.override.name == 'test-config.groovy'

        cleanup:
        externalConfigFile.delete()
        applicationConfigFile.delete()
    }

    @DirtiesRuntime
    void "test addExternalConfig method when external file locations is not present"() {
        given: "File instances with configurations and other required instances"
        File applicationConfigFile = new File('application-test.groovy')
        applicationConfigFile.createNewFile()
        applicationConfigFile << "grails.mail.overrideAddress = 'unit.test@causecode.com'\n" +
                "grails.original.appName = 'nucleus'\n"

        GrailsAutoConfiguration application = new GrailsAutoConfiguration()
        StandardServletEnvironment environment = new StandardServletEnvironment()

        // Adding application configurations in environment.
        ConfigObject config = new ConfigSlurper(Environment.current.name)
                .parse(new File(applicationConfigFile.absolutePath).toURI().toURL())
        environment.propertySources.addFirst(new MapPropertySource('app-config', config))
        assert environment.propertySources.propertySourceList[0].source.grails.mail.overrideAddress ==
                'unit.test@causecode.com'
        assert environment.propertySources.propertySourceList[0].source.grails.original.appName == 'nucleus'
        assert environment.propertySources.propertySourceList[0].source.grails.config.locations == [:]

        and: "Mocked method"
        ClassLoader.metaClass.getResource = { String name ->
            return applicationConfigFile.toURI().toURL()
        }

        when: "addExternalConfig method is called and external configuration file is not given"
        NucleusUtils.addExternalConfig(application, environment)

        then: "Properties remain unchanged"
        assert environment.propertySources.propertySourceList[0].source.grails.mail.overrideAddress ==
                'unit.test@causecode.com'
        assert environment.propertySources.propertySourceList[0].source.grails.original.appName == 'nucleus'

        when: "addExternalConfig method is called and external configuration file path is given but does not exist"
        applicationConfigFile << "test { \n grails.config.locations =" +
                "['${System.getProperty('user.dir') + '/temp/external-config.groovy'}'] \n } \n" +
                "development { \n grails.config.locations =" +
                "['${System.getProperty('user.dir') + '/temp/external-config.groovy'}'] \n }"

        NucleusUtils.addExternalConfig(application, environment)

        then: "Method throws FileNotFoundException"
        FileNotFoundException exception = thrown()
        exception.message == System.getProperty('user.dir') + '/temp/external-config.groovy' +
                ' (No such file or directory)'

        cleanup:
        applicationConfigFile.delete()
    }

    void "test getMergedConfigurations configurations to override configurations"() {
        when: "getMergedConfiguration method is called"
        ConfigObject resultConfigObject = NucleusUtils.getMergedConfigurations('com.causecode.test.files.PluginConfig')

        then: "Method returns a ConfigObject which has merged configurations from AppConfig and plugin"
        resultConfigObject.grails.test.pluginConfig == 'Configuration from PluginConfig.groovy file'
        resultConfigObject.grails.test.appConfig == 'Configuration from application.groovy file'
        // appName has been overridden in application.groovy file.
        resultConfigObject.grails.test.appName == 'appName from application.groovy file'
    }
}
