/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.embedded

import com.causecode.mongo.embeddable.EmbeddableDomain

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

    static constraints = {
        email blank: false, email: true, unique: true
        gender inList: ['male', 'female', 'unspecified'], size: 4..11, nullable: true
        username blank: false, unique: true
        firstName maxSize: 100, nullable: true
        lastName maxSize: 100, nullable: true
        pictureURL nullable: true
    }

    EmUser(Map params) {
        this.instanceId = params.instanceId
        this.accountExpired = params.accountExpired
        this.accountLocked = params.accountLocked
        this.enabled = params.enabled
        this.email = params.email
        this.firstName = params.firstName
        this.gender = params.gender
        this.lastName = params.lastName
        this.username = params.username
        this.pictureURL = params.pictureURL
    }
}
