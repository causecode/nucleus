/*
 * Copyright (c) 2016, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package nucleus

import com.causecode.util.NucleusUtils
import com.causecode.util.EmailService
import grails.artefact.Artefact
import grails.plugins.Plugin
import com.causecode.util.StringAsGspRenderer

/**
 * Contains all information about Nucleus Plugin
 */
class NucleusGrailsPlugin extends Plugin {

    def grailsVersion = '3.1.4 > *'
    def pluginExcludes = [
        '**/nucleus/UrlMappings*/**'
    ]

    def title = 'Nucleus Plugin'
    def author = 'CauseCode'
    def authorEmail = 'shashank.agrawal@causecode.com'
    def description = '''\
            Brief summary/description of the plugin.
        '''

    def documentation = 'https://bitbucket.org/causecode/nucleus'

    Closure doWithSpring() { { ->
            // Implement runtime spring config (optional)
            stringAsGspRenderer(StringAsGspRenderer) {
                groovyPageRenderer = ref('groovyPageRenderer')
            }
        }
    }
    @Override
    void doWithApplicationContext() {
        NucleusUtils.initialize(applicationContext)
    }

    void doWithDynamicMethods() {
        EmailService emailService = applicationContext.getBean(EmailService)

        List<Artefact> listOfArtefacts = grailsApplication.controllerClasses
        listOfArtefacts.addAll(grailsApplication.serviceClasses)

        for (artefact in listOfArtefacts) {
            artefact.metaClass.sendEmail = { Closure closure, String eventName ->
                emailService.sendEmail(closure, eventName)
            }
        }
    }
}
