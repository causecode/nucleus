/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.seo.sitemap

import groovy.transform.EqualsAndHashCode

/**
 * Sitemap groovy class used to specify has many URL relationship.
 */
@EqualsAndHashCode
class Sitemap {
    static hasMany = [urlset: Url]

    @Override
    String toString() {
        return "Sitemap [$id]"
    }
}
