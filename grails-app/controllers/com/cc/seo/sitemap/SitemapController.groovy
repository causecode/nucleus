/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.seo.sitemap

import grails.converters.XML
import grails.plugins.springsecurity.Secured

@Secured(["ROLE_ADMIN"])
class SitemapController {

    def sitemapService

    def generate() {
        sitemapService.generate()
        flash.message = "Site map generated."
        flash.timeout = "clear"
        redirect uri: "/dashboard"
    }

    @Secured(["permitAll"])
    def index = {
        List sitemapList = Sitemap.list()
        def sitemap = sitemapList.pop()
        render sitemap as XML
    }

}