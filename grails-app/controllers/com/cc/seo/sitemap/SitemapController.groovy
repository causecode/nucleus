/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.seo.sitemap

import grails.plugin.springsecurity.annotation.Secured

@Secured(["ROLE_ADMIN"])
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
        flash.message = "Site map generated."
        flash.timeout = "clear"
        redirect uri: "/dashboard"
    }

   /**
     * Default action : Responds Sitemap first object or new sitemap Object.
     * Reverting back the commit: https://bitbucket.org/causecode/nucleus/commits/a4500461f761f69bcf39372d03a5b3fe58b84dce
     * Replaced respond with render, since it was adding an extra xml <list> element to the generated
     * sitemap.xml
     */
    @Secured(["permitAll"])
    def index() {
        render((Sitemap.first() ?: EMPTY_SITEMAP) as XML)
    }

}
