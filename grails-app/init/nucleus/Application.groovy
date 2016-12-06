package nucleus

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

/**
 * Entry point of the Grails App.
 */
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}
