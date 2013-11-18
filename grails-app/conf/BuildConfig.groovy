grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    inherits("global") {
    }

    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {
        grailsCentral()
        mavenLocal()
    }

    dependencies {
    }

    plugins {
        compile group: "com.cc.plugins", name: "file-uploader", version: "2.3", export: false
        build(":tomcat:$grailsVersion", ":release:2.0.3", ":rest-client-builder:1.0.2") {
            export = false
        }
        runtime ":hibernate:$grailsVersion"
        compile (":spring-security-core:1.2.7.3") {
            export = false
        }
    }
}
