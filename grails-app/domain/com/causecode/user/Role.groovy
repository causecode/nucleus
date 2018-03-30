/*
 * Copyright (c) 2011-Present CauseCode Technologies Pvt Ltd, India.
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

    Date dateCreated
    Date lastUpdated

    static mapping = {
        cache true
    }

    static constraints = {
        authority blank: false, unique: true
        dateCreated bindable: false
        lastUpdated bindable: false
    }
}
