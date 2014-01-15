grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
    inherits("global") {
    }

    log "warn"

    repositories {
        mavenRepo "http://maven.causecode.com"
        grailsCentral()
        mavenLocal()
        mavenCentral()
        mavenRepo "http://repo.spring.io/milestone/"
    }

    dependencies {
    }

    plugins {
        compile("com.cc.plugins:content:2.1.2", "com.cc.plugins:file-uploader:2.4-SNAPSHOT") {
            export = false
        }
        build(":tomcat:7.0.42", ":release:3.0.1", ":rest-client-builder:1.0.3") {
            export = false
        }
        compile (":spring-security-core:1.2.7.3", ":taggable:1.0.1") {
            export = false
        }
    }
}
