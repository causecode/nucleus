package com.causecode.seo.sitemap

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * This class specifies unit test cases for {@link com.causecode.seo.sitemap.Url}.
 */
@Mock(Sitemap)
@TestFor(Url)
class UrlSpec extends Specification {

    void 'test toString() method'() {
        when: 'Url instance is given and toString is called'
        Url userUrl = new Url([changefreq: 'month', loc: 'https://causecode.com/users', priority: 1.0f])
        assert userUrl.save(flush: true, failOnError: true)
        String result = userUrl

        then: 'result must match with the given String'
        result == 'Url(1)'
    }
}
