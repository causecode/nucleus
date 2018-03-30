package com.causecode.seo.sitemap

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * This class specifies unit test cases for {@link com.causecode.seo.sitemap.Sitemap}.
 */
@Mock(Url)
@TestFor(Sitemap)
class SitemapSpec extends Specification {

    void 'test Sitemap relationship with Url'() {
        given:
        Sitemap causecodeSitemap = new Sitemap()
        Url blog = new Url([changefreq: 'month', loc: 'https://causecode.com/blogs', priority: 1.0f])
        Url user = new Url([changefreq: 'month', loc: 'https://causecode.com/users', priority: 1.0f])
        Url job = new Url([changefreq: 'month', loc: 'https://causecode.com/jobs', priority: 1.0f])

        when: 'Urls are added to sitemap'
        causecodeSitemap.addToUrlset(blog)
        causecodeSitemap.addToUrlset(user)
        causecodeSitemap.addToUrlset(job)

        assert causecodeSitemap.save(flush: true, failOnError: true)
        assert Sitemap.count() == 1

        then: 'Urls must get persisted with Sitemap'
        Url.count() == 3
    }

    void 'test toString() method'() {
        when: 'Sitemap instance is given and toString is called'
        Url user = new Url([changefreq: 'day', loc: 'https://causecode.com/users', priority: 1.0f])

        Sitemap causecodeSitemap = new Sitemap()
        causecodeSitemap.addToUrlset(user)
        assert causecodeSitemap.save(flush: true, failOnError: true)
        String result = causecodeSitemap

        then: 'result must match with the given String'
        result == 'Sitemap(1)'
    }
}
