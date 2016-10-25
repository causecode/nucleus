/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.user

import groovy.transform.EqualsAndHashCode

/**
 * Role groovy class used to specify authority information.
 */
@EqualsAndHashCode
class Role {

    String authority

    static mapping = {
        cache true
    }

    static constraints = {
        authority blank: false, unique: true
    }

    @Override
    String toString() {
        return "Role [$id]"
    }
}
