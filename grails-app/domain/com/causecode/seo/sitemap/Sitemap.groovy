/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.causecode.seo.sitemap

/**
 * Sitemap groovy class used to specify has many URL relationship .
 */
class Sitemap {
 
    static hasMany = [urlset: Url]

}
