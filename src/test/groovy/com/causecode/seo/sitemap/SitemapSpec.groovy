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

@Mock(Url)
@TestFor(Sitemap)
class SitemapSpec extends Specification {

    def 'test Sitemap relationship with Url'() {
        given:
        Sitemap causecodeSitemap = new Sitemap()

        Url blog = new Url(changefreq: 'change', loc: 'https://causecode.com/blogs', priority: 1.0f)

        Url user = new Url(changefreq: 'change', loc: 'https://causecode.com/users', priority: 1.0f)

        Url job = new Url(changefreq: 'change', loc: 'https://causecode.com/jobs', priority: 1.0f)

        when: 'Urls are added to sitemap'
        causecodeSitemap.addToUrlset(blog)
        causecodeSitemap.addToUrlset(user)
        causecodeSitemap.addToUrlset(job)

        assert causecodeSitemap.save(flush: true, failOnError: true)
        assert Sitemap.count() == 1

        then: 'Urls must get persisted with Sitemap'
        Url.count() == 3
    }

    def 'test toString() method'() {
        when: 'Sitemap instance is given and toString is called'
        Url user = new Url(changefreq: 'change', loc: 'https://causecode.com/users', priority: 1.0f)

        Sitemap causecodeSitemap = new Sitemap()
        causecodeSitemap.addToUrlset(user)
        assert causecodeSitemap.save(flush: true, failOnError: true)
        String result = causecodeSitemap.toString()

        then: 'result must match with the given String'
        result == 'Sitemap [1]'
    }
}
