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
        '**/nucleus/UrlMappings*/**',
        '**/com/causecode/user/**'
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
}
