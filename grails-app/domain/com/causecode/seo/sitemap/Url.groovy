package com.causecode.seo.sitemap

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Url groovy class used to specify information related to URL's.
 */
@ToString(includes = ['id'], includePackage = false)
@EqualsAndHashCode
class Url {

    float priority = 0.0

    String changefreq
    String loc

    static belongsTo = [Sitemap]

    static constraints = {
        loc url: true
        changefreq nullable: true
    }
}
