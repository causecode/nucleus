/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package nucleus

import com.causecode.util.NucleusUtils
import grails.plugins.Plugin
import com.causecode.util.StringAsGspRenderer

/**
 * Contains all information about Nucleus Plugin
 */
class NucleusGrailsPlugin extends Plugin {

    def grailsVersion = '3.1.4 > *'
    def pluginExcludes = [
        'grails-app/views/error.gsp'
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

    @Override
    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    @Override
    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    @Override
    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    @Override
    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
