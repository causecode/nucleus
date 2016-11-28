/*
 * Copyright (c) 2016, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.seo.sitemap

import groovy.transform.EqualsAndHashCode

/**
 * Url groovy class used to specify information related to URL's.
 */
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

    @Override
    String toString() {
        return "Url [$id]"
    }
}
