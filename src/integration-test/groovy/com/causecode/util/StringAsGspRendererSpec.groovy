/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.causecode.util

import grails.test.spock.IntegrationSpec
import grails.util.Environment

import com.causecode.user.User

class StringAsGspRendererSpec extends IntegrationSpec {

    StringAsGspRenderer stringAsGspRenderer
    User userInstance

    def cleanup() {
        userInstance.delete(flush: true)
    }

    // Get number of files inside a directory
    private int filesInDirectory(File directory) {
        return directory.listFiles().findAll { it.isFile() && it.name.endsWith(".gsp") }.size()
    }

    def setup() {
        userInstance = new User([username: "dummy1", password: "dummy@13", email: "dummy@something.com",
            firstName: "Dummy", lastName: "User", gender: "male"])

        userInstance.save(flush: true)
        assert userInstance.id != null
    }

    void "test cleanup template cache for development & test"() {
        given: "Some files available in the template cache folder"

        stringAsGspRenderer.renderFromDomain(userInstance, "firstName", [:])

        assert filesInDirectory(new File(stringAsGspRenderer.TEMPLATE_CACHE_DIRECTORY_PATH)) == 1

        when: "Cleanup is called"

        stringAsGspRenderer.cleanupTemplateCache()

        then: "No template cache file will be found"

        assert filesInDirectory(new File(stringAsGspRenderer.TEMPLATE_CACHE_DIRECTORY_PATH)) == 0
    }

    void "test cleanup template cache for production & other environment"() {
        given: "Some files available in the template cache folder for production env"

        Environment.executeForEnvironment(Environment.PRODUCTION) {
            stringAsGspRenderer.renderFromDomain(userInstance, "firstName", [:])
        }

        Environment.executeForEnvironment(Environment.PRODUCTION) {
            assert filesInDirectory(new File(stringAsGspRenderer.TEMPLATE_CACHE_DIRECTORY_PATH)) == 1
        }

        when: "Cleanup is called"

        Environment.executeForEnvironment(Environment.PRODUCTION) {
            stringAsGspRenderer.cleanupTemplateCache()
        }

        then: "No template cache file will be found in production env page"

        assert filesInDirectory(new File(stringAsGspRenderer.TEMPLATE_CACHE_DIRECTORY_PATH)) == 0
    }
}