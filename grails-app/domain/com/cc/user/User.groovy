/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.user

import org.grails.databinding.BindingFormat

import com.cc.annotation.sanitizedTitle.SanitizedTitle

class User {

    transient springSecurityService

    boolean accountExpired
    boolean accountLocked
    boolean enabled = true
    boolean passwordExpired

    @BindingFormat('MM/dd/yyyy')
    Date birthdate
    Date dateCreated
    Date lastUpdated

    String email
    String firstName
    @SanitizedTitle
    String fullName
    String gender
    String lastName
    String password
    String username

    static transients = ["springSecurityService", "fullName"]

    static constraints = {
        email blank: false, email: true, unique: true
        gender inList: ["male", "female"], size: 4..6
        password blank: false
        username blank: false, unique: true
        birthdate max: new Date().clearTime()
    }

    static mapping = {
        password column: '`password`'
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role } as Set
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = springSecurityService.encodePassword(password)
    }

    @Override
    String toString() {
        return "User [$username][$id]"
    }

    String getFullName() {
        return firstName + " " + lastName
    }

}