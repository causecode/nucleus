/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.user

import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * UserRole join groovy class specifies authority for user.
 */
class UserRole implements Serializable {

    private static final long serialVersionUID = 1

    User user
    Role role

    @Override
    boolean equals(other) {
        if (!(other instanceof UserRole)) {
            return false
        }

        other.user?.id == user?.id && other.role?.id == role?.id
    }

    @Override
    int hashCode() {
        def builder = new HashCodeBuilder()
        if (user) builder.append(user.id)
        if (role) builder.append(role.id)
        builder.toHashCode()
    }

    static UserRole get(long userId, long roleId) {
        UserRole.where {
            user == User.load(userId) && role == Role.load(roleId)
        }.get()
    }

    static UserRole create(User user, Role role, boolean flush = false) {
        new UserRole(user: user, role: role).save(flush: flush, insert: true)
    }

    static UserRole create(User user, String role = "ROLE_USER", flush = false) {
        new UserRole(user: user, role: Role.findOrSaveByAuthority(role)).save(flush: flush, insert: true)
    }

    static boolean remove(User u, Role r, boolean flush = false) {
        int rowCount = UserRole.where {
            user == User.load(u.id) && role == Role.load(r.id)
        }.deleteAll()

        rowCount > 0
    }

    /**
     * Remove all role of a given User by removing instance of UserRole
     * for particular User.
     * @param u Instance of {@link com.cc.user.User User}
     */
    static void removeAll(User u) {
        UserRole.findAllByUser(u)*.delete(flush: true)  // To avoid session flush problems
    }

    /**
     * Remove all instances of UserRole for a given role.
     * Useful when wanted to revoke a particular role from every User.
     * @param roleInstance
     */
    static void removeAll(Role roleInstance) {
        UserRole.where {
            role == Role.load(roleInstance.id)
        }.deleteAll()
    }

    static mapping = {
        id composite: ['role', 'user']
        version false
    }

}