/*
 * Copyright (c) 2016, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.seo.sitemap

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Sitemap groovy class used to specify has many URL relationship.
 */
@ToString(includes = ['id'], includePackage = false)
@EqualsAndHashCode
class Sitemap {
    static hasMany = [urlset: Url]
}
