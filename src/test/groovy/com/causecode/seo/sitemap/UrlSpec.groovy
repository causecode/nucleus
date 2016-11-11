/*
 * Copyright (c) 2011-Present CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.seo.sitemap

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

@Mock(Sitemap)
@TestFor(Url)
class UrlSpec extends Specification {

    void 'test toString() method'() {
        when: 'Url instance is given and toString is called'

        Url userUrl = new Url(changefreq: 'change', loc: 'https://causecode.com/users', priority: 1.0f)
        assert userUrl.save(flush: true, failOnError: true)

        String result = userUrl.toString()
        then: 'result must match with the given String'
        result == 'Url [1]'
    }
}
