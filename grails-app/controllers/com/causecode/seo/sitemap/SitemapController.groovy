package com.causecode.seo.sitemap

import grails.converters.XML
import grails.plugin.springsecurity.annotation.Secured

/**
 * Defines end point for Sitemap
 */
@Secured(['ROLE_ADMIN'])
class SitemapController {

    private static final Sitemap EMPTY_SITEMAP = new Sitemap()

    /**
     * Dependency injection for the sitemapService.
     */
    def sitemapService

    /**
     * Generate Sitemap object and redirects to dashboard.
     */
    def generate() {
        sitemapService.generate()
        redirect uri: '/dashboard'
    }

   /**
     * Default action : Responds Sitemap first object or new sitemap Object.
     * Reverting back the commit:
     * https://bitbucket.org/causecode/nucleus/commits/a4500461f761f69bcf39372d03a5b3fe58b84dce
     * Replaced respond with render, since it was adding an extra xml <list> element to the generated
     * sitemap.xml
     */
    @Secured(['permitAll'])
    def index() {
        render((Sitemap.first() ?: EMPTY_SITEMAP) as XML)
    }

}
