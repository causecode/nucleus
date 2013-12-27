/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.util

import grails.converters.XML

import org.codehaus.groovy.grails.web.converters.Converter
import org.codehaus.groovy.grails.web.converters.marshaller.NameAwareMarshaller
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller

import com.cc.seo.sitemap.Sitemap

class SitemapMarshaller implements ObjectMarshaller, NameAwareMarshaller {

    public boolean supports(Object object) {
        return object instanceof Sitemap
    }

    public void marshalObject(Object object, Converter<XML> converter) {
        Sitemap sitemap = object as Sitemap
        converter.attribute 'xmlns', "http://www.sitemaps.org/schemas/sitemap/0.9"
        sitemap.urlset.each {
            converter.startNode 'url'
            converter.startNode 'loc'
            converter.chars it.loc
            converter.end()

            if(it.changefreq) {
                converter.startNode 'changefreq'
                converter.chars Float.toString(it.changefreq)
                converter.end()
            }

            converter.startNode 'priority'
            converter.chars Float.toString(it.priority)
            converter.end()

            converter.end()
        }
    }

    String getElementName(Object o) {
        return 'urlset'
    }

}