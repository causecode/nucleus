/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.embedded

import com.causecode.mongo.embeddable.EmbeddableDomain
import org.bson.types.ObjectId

/**
 * This class represents embedded instance of User domain.
 */
class EmUser implements EmbeddableDomain {

    Long instanceId
    boolean accountExpired
    boolean accountLocked
    boolean enabled = true

    String email
    String firstName
    String gender
    String lastName
    String username
    String pictureURL

    EmUser() {
    }

    EmUser( Long instanceId, boolean accountExpired, boolean accountLocked, boolean enabled = true, String email,
                    String firstName, String gender, String lastName, String username, String pictureURL) {
        this.instanceId = instanceId
        this.accountExpired = accountExpired
        this.accountLocked = accountLocked
        this.enabled = enabled
        this.email = email
        this.firstName = firstName
        this.gender = gender
        this.lastName = lastName
        this.username = username
        this.pictureURL = pictureURL
    }
}
