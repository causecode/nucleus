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
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

@TestMixin(GrailsUnitTestMixin)
@TestFor(SitemapController)
@Mock([Sitemap, Url])
class SitemapControllerSpec extends Specification {

    void 'test index action'() {
        when: 'index action is called and sitemap is null'
        controller.index()

        then: 'empty sitemap in xml format is generated'
        controller.response.text == '<?xml version="1.0" encoding="UTF-8"?><sitemap><urlset /></sitemap>'
    }

    void 'test index action when Sitemap is not null'() {
        given: 'Sitemap instance is given'
        Sitemap causecodeSitemap = new Sitemap()
        Url blog = new Url(changefreq: '10', loc: 'https://causecode.com/blogs', priority: 1.0f)
        Url user = new Url(changefreq: '5', loc: 'https://causecode.com/users', priority: 1.0f)
        Url job = new Url(changefreq: '2', loc: 'https://causecode.com/jobs', priority: 1.0f)
        causecodeSitemap.addToUrlset(blog)
        causecodeSitemap.addToUrlset(user)
        causecodeSitemap.addToUrlset(job)
        assert causecodeSitemap.save(flush: true, failOnError: true)
        assert Sitemap.count() == 1
        assert Url.count() == 3

        when: 'index action is called and sitemap is not null'
        controller.index()

        then: 'First sitemap instance will be generated'
        controller.response.text == '<?xml version="1.0" encoding="UTF-8"?><sitemap id="1"><urlset><url id="1" /><url id="2" /><url id="3" /></urlset></sitemap>'
    }

    void 'test generate method'() {
        given: 'Sitemap Service'
        def siteMapService = new Object()
        siteMapService.metaClass.generate = { println 'Sitemap generated' }
        controller.sitemapService = siteMapService

        when: 'generate method is called'
        controller.generate()

        then: 'user must be redirected to dashboard'
        controller.response.redirectedUrl == '/dashboard'
    }
}
