package com.causecode.util

import grails.converters.XML

import org.grails.web.converters.Converter
import org.grails.web.converters.marshaller.NameAwareMarshaller
import org.grails.web.converters.marshaller.ObjectMarshaller

import com.causecode.seo.sitemap.Sitemap

/**
 * A generic Marshaller for {@link Sitemap}.
 * @author Shashank Agrawal
 *
 */

class SitemapMarshaller implements ObjectMarshaller<XML>, NameAwareMarshaller {

    /**
     * Checks Object is instance of Sitemap or not.
     * @param object The {@link Object}
     * @return Boolean value checking object accepted is instance of Sitemap or not.
     */
    //TODO remove instanceOf check
    @SuppressWarnings(['Instanceof'])
    boolean supports(Object object) {
        return object instanceof Sitemap
    }

    /**
     * This method used to marshal object into XML.
     * @param object The {@link Object}
     * @param converter {@link Converter} instance used to convert object.
     */
    void marshalObject(Object object, Converter converter) {
        Sitemap sitemap = object as Sitemap
        converter.attribute 'xmlns', 'http://www.sitemaps.org/schemas/sitemap/0.9'
        sitemap.urlset.each {
            converter.startNode 'url'
            converter.startNode 'loc'
            converter.chars it.loc
            converter.end()

            if (it.changefreq) {
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

    /**
     * Return tag name for the Object.
     * @param o The {@link Object}
     * @return Return tag name for the Object.
     */
    @Override
    String getElementName(Object o) {
        return 'urlset'
    }

}
