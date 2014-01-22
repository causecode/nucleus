/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.seo.sitemap

import grails.rest.Resource

@Resource(uri = "/sitemap", formats = ["xml"])
class Sitemap {

    static hasMany = [urlset: Url]

}