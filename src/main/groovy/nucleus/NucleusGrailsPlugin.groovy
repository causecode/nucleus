/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package nucleus

import com.causecode.util.NucleusUtils
import grails.plugins.*
import com.causecode.util.StringAsGspRenderer

class NucleusGrailsPlugin extends Plugin {

    def grailsVersion = "3.1.4 > *"
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = "Nucleus Plugin"
    def author = "CauseCode"
    def authorEmail = "shashank.agrawal@causecode.com"
    def description = '''\
            Brief summary/description of the plugin.
        '''

    def documentation = "https://bitbucket.org/causecode/nucleus"

    Closure doWithSpring() { {->
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
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
