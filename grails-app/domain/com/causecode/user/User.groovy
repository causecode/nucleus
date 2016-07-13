/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.causecode.user

import org.grails.databinding.BindingFormat

/**
 * User groovy class used to specify person entity with default information.
 */
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
    String fullName
    String gender
    String lastName
    String password
    String username

    static transients = ["springSecurityService", "fullName"]

    static constraints = {
        email blank: false, email: true, unique: true
        gender inList: ["male", "female"], size: 4..6, nullable: true
        password blank: false
        username blank: false, unique: true
        birthdate nullable: true, max: new Date().clearTime()
        firstName maxSize: 100
        lastName maxSize: 100
    }

    static mapping = {
        password column: '`password`'
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role } as Set
    }

    def beforeInsert() {
        this.email = this.email.toLowerCase()
        encodePassword()
    }

    def beforeUpdate() {
        this.email = this.email.toLowerCase()
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
/*

    static void executeUpdate(Map requestData, List selectedUserIds) {
        executeQuery("UPDATE User SET enabled = :actionType WHERE id IN :userIds", [
                actionType: requestData.type, userIds: selectedUserIds])
    }
*/

}