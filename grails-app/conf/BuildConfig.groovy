grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
    inherits("global") {
    }

    log "warn"

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
        mavenRepo "http://repo.spring.io/milestone/"
    }

    dependencies {
        test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"
    }

    plugins {
        build(":tomcat:7.0.55", ":release:3.0.1", ":rest-client-builder:2.0.1") {
            export = false
        }
        compile (":spring-security-core:2.0-RC3") {
            export = false
        }
        compile (":hibernate:3.6.10.18") {
            export = false
        }
        //plugins for compile step
        compile ':cache:1.1.3'
    }
}
