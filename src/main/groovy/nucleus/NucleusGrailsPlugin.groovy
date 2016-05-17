/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package nucleus

import grails.plugins.*
import com.causecode.util.StringAsGspRenderer

class NucleusGrailsPlugin extends Plugin {

    def name = "nucleus"
    def version = "0.3.4"

    def grailsVersion = "3.1.4"
    def groupId = "com.cc.plugins"
    def dependsOn = [:]
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

    def doWithSpring = {
        stringAsGspRenderer(StringAsGspRenderer) {
            groovyPageRenderer = ref('groovyPageRenderer')
        }
    }
}
