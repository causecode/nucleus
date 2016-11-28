/*
 * Copyright (c) 2016, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.user

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Role groovy class used to specify authority information.
 */
@ToString(includes = ['id'], includePackage = false)
@EqualsAndHashCode
class Role {

    String authority

    static mapping = {
        cache true
    }

    static constraints = {
        authority blank: false, unique: true
    }
}
