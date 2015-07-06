import grails.util.Environment

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

String command = System.getProperty("sun.java.command")
boolean isAPluginBuild = command.contains("maven-") || command.contains("release-plugin")

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
    inherits("global") {
    }

    log "warn"

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
        mavenRepo "http://repo.grails.org/grails/core"
    }

    dependencies {
        test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"

        // Do not include following at the time of releasing the plugin
        if (!isAPluginBuild) {
            runtime 'mysql:mysql-connector-java:5.1.29' // Driver for MySql JDBC
            compile 'commons-beanutils:commons-beanutils:1.8.3'
        }
    }

    plugins {
        build(":tomcat:7.0.55", ":release:3.0.1", ":rest-client-builder:2.0.1") {
            export = false
        }
        compile (":spring-security-core:2.0-RC3", ":export:1.5") {
            export = false
        }

        // Do not include hibernate while in development
        // But do include them while packaging like "maven-install" or "maven-deploy"
        if (!Environment.isDevelopmentMode() || isAPluginBuild) {
            compile (":hibernate4:4.3.8.1", ":cache:1.1.3") {
                export = false
            }
        }
    }
}
