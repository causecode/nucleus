package com.causecode.user.embedded

import com.causecode.mongo.embeddable.EmbeddableDomain
import org.bson.types.ObjectId

/**
 * This class represents embedded instance of User domain.
 */
class EmUser implements EmbeddableDomain {

    ObjectId instanceId
    boolean accountExpired
    boolean accountLocked
    boolean enabled = true

    String email
    String firstName
    String lastName
    String username

    EmUser() {
    }

    static constraints = {
        email blank: false, email: true
        firstName maxSize: 100, nullable: true
        lastName maxSize: 100, nullable: true
    }

    EmUser(Map params) {
        this.instanceId = params.instanceId
        this.accountExpired = params.accountExpired
        this.accountLocked = params.accountLocked
        this.enabled = params.enabled
        this.email = params.email
        this.firstName = params.firstName
        this.lastName = params.lastName
        this.username = params.username
    }

    Map<String, String> getFieldsAsMap() {
        return [
                'instanceId': this.instanceId,
                'accountExpired': this.accountExpired,
                'accountLocked': this.accountLocked,
                'enabled': this.enabled,
                'email': this.email,
                'firstName': this.firstName,
                'lastName': this.lastName,
                'username': this.username
        ]
    }
}
