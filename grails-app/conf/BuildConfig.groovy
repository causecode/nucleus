grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
    inherits("global") {
    }

    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
        mavenRepo "http://repo.spring.io/milestone/"
    }

    dependencies {
    }

    plugins {
        compile group: "com.cc.plugins", name: "file-uploader", version: "2.3", export: false
        compile group: "com.cc.plugins", name: "content", version: "2.1", export: false
        build(":tomcat:7.0.42", ":release:3.0.1", ":rest-client-builder:1.0.3", ":hibernate:3.6.10.3") {
            export = false
        }
        compile (":spring-security-core:1.2.7.3", ":taggable:1.0.1") {
            export = false
        }
    }
}
