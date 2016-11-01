/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.causecode.user

/**
 * Role groovy class used to specify authority information.
 */
class Role {

    static transients = ['ROLE_CONTENT_MANAGER', 'ROLE_EMPLOYEE', 'PERMIT_ALL']

    String authority

    static mapping = {
        cache true
    }

    static constraints = {
        authority blank: false, unique: true
    }

    static final String ROLE_CONTENT_MANAGER = 'ROLE_CONTENT_MANAGER'
    static final String ROLE_EMPLOYEE = 'ROLE_EMPLOYEE'
    static final String PERMIT_ALL = 'permitAll'
}
