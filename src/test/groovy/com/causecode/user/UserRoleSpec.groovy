/*
 * Copyright (c) 2011 - Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.user

import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.runtime.DirtiesRuntime
import spock.lang.Specification

@Mock([User, Role, SpringSecurityService])
@TestFor(UserRole)
class UserRoleSpec extends Specification {

    User admin, normalUser
    Role adminRole, normalUserRole
    UserRole adminUserRole, userRole

    def setup() {
        admin = new User([firstName: 'admin', lastName: 'admin', username: 'admin', password: 'admin@123', email: 'admin@causecode.com'])

        SpringSecurityService springSecurityServiceForAdminUser = new SpringSecurityService()
        springSecurityServiceForAdminUser.metaClass.encodePassword = { String password -> 'ENCODED_PASSWORD' }
        admin.springSecurityService = springSecurityServiceForAdminUser
        assert admin.save(flush: true, failOnError: true)

        adminRole = new Role([authority: 'ROLE_ADMIN'])
        assert adminRole.save(flush: true, failOnError: true)

        adminUserRole = UserRole.create(admin, adminRole, true)
        assert adminUserRole

        normalUser = new User([firstName: 'normal', lastName: 'normal', username: 'normal', password: 'normal@123', email: 'normal@causecode.com'])

        SpringSecurityService springSecurityServiceForNormalUser = new SpringSecurityService()
        springSecurityServiceForNormalUser.metaClass.encodePassword = { String password -> 'ENCODED_PASSWORD' }
        normalUser.springSecurityService = springSecurityServiceForNormalUser
        assert normalUser.save(flush: true, failOnError: true)

        normalUserRole = new Role([authority: 'ROLE_USER'])
        assert normalUserRole.save(flush: true, failOnError: true)

        userRole = UserRole.create(normalUser, normalUserRole, true)
        assert userRole
    }

    void 'test equals method for instance which is not instanceof UserRole'() {
        when: 'equals method is called'
        boolean result = adminUserRole.equals(adminRole)

        then: 'equals method must return false'
        result == false
    }

    void 'test equals method'() {
        when: 'equals method is called with two difference instances of UserRole'
        boolean result = adminRole.equals(userRole)

        then: 'equals method must return false'
        result == false

        when: 'equals method is called with two same instances of UserRole'
        result = adminRole.equals(adminRole)

        then: 'equals method must return true'
        result == true
    }

    void 'test equals method when passed instance is null'() {
        when: 'equals method is called'
        boolean result = adminUserRole.equals(null)

        then: 'equals method must return false'
        result == false
    }

    void 'test hashCode method'() {
        when: 'hashCode method is called'
        assert adminRole.save(flush: true, failOnError: true)
        UserRole mainAdminRole = UserRole.create(admin, adminRole, true)
        assert mainAdminRole
        int hashCode = mainAdminRole.hashCode()

        then: 'hashCode value must end with 11'
        String.valueOf(hashCode).endsWith('11')
    }

    void 'test get(long userId, long roleId) method'() {
        when: 'get(long userId, long roleId) method is called'
        UserRole userRole = UserRole.get(admin.id, adminRole.id)

        then: 'Returned instance must match with the instance created above'
        userRole.equals(adminUserRole)
    }

    void 'test get() method when passed id is not valid'() {
        when: 'get(long userId, long roleId) method is called'
        UserRole userRole = UserRole.get(0L, 0L)

        then: 'null result'
        userRole == null
    }

    @DirtiesRuntime
    void 'test create method when passed role as a Instance is passed'() {
        given: 'User and Role instance'
        int userRoleCount = UserRole.count()
        assert userRoleCount == 2
        User newAdmin = createAdminUser()

        when: 'create(User admin, Role role, true) is called'
        UserRole newAdminUserRole = UserRole.create(newAdmin, adminRole, true)
        assert newAdminUserRole

        then: 'Created Instance must be persisted into database'
        UserRole savedInstance = UserRole.where {
            user == User.load(newAdmin.id) && role == Role.load(adminRole.id)
        }.get()

        savedInstance.equals(newAdminUserRole)
        UserRole.count() == userRoleCount + 1
    }

    void 'test create method when passed user or role instance is null'() {
        when: 'create method is called with null user or role'
        UserRole.create(null, null, true)

        then: 'Exception is thrown'
        thrown(Exception)
    }

    @DirtiesRuntime
    void 'test create method when role is not passed at all'() {
        given: 'User instance'
        int userRoleCount = UserRole.count()
        assert userRoleCount == 2
        User newAdmin = createAdminUser()

        when: 'Create method is called'
        UserRole createdUserRole = UserRole.create(newAdmin)
        assert createdUserRole

        then: 'UserRole will be created with voidault ROLE_USER'
        Role role = Role.findByAuthority('ROLE_USER')
        UserRole savedInstance = UserRole.get(newAdmin.id, role.id)
        savedInstance.equals(createdUserRole)
        UserRole.count() == userRoleCount + 1
    }

    @DirtiesRuntime
    void 'test create method when passed role as as String is passed'() {
        given: 'User instance'
        int userRoleCount = UserRole.count()
        assert userRoleCount == 2
        User newAdmin = createAdminUser()
        String role = 'ROLE_ADMIN'

        when: 'create method is called'
        UserRole createdUserRole = UserRole.create(newAdmin, role, true)
        assert createdUserRole

        then: 'Created instance must be persisted into database'
        Role newAdminRole = Role.findOrSaveByAuthority(role)
        UserRole savedInstance = UserRole.where {
            user == User.load(newAdmin.id) && role == Role.load(newAdminRole.id)
        }.get()
        savedInstance.equals(createdUserRole)
        UserRole.count() == userRoleCount + 1
    }

    void 'test remove method'() {
        when: 'Remove method is called and user, role exist'
        assert UserRole.count() == 2
        boolean result = UserRole.remove(admin, adminRole)

        then: 'true value must be returned indicating successful deletion'
        result == true
        UserRole.count() == 1

        when: 'Remove method is called and user, role do not exist'
        User user = new User()
        Role role = new Role()
        result = UserRole.remove(user, role)

        then: 'false value must be returned indicating no rows were deleted'
        result == false
    }

    void 'test removeAll method to delete all user roles by passing user instance'() {
        given: 'UserRole instance'
        Role managerRole = new Role([authority: 'ROLE_USER_MANAGER'])
        assert managerRole.save(flush: true, failOnError: true)

        UserRole adminUserRole = UserRole.create(admin, adminRole, true)
        UserRole adminUserManagerRole = UserRole.create(admin, managerRole, true)

        assert adminUserRole
        assert adminUserManagerRole

        when: 'removeAll method is called'
        UserRole.removeAll(admin)

        then: 'All userRoles associated with the user must be deleted'
        UserRole.get(admin.id, adminRole.id) == null
        UserRole.get(admin.id, managerRole.id) == null
    }

    @DirtiesRuntime
    void 'test removeAll method to delete all user roles by passing role instance'() {
        given: 'UserRole instance for two users'
        User adminOne = new User([firstName: 'adminOne', lastName: 'adminOne', username: 'adminOne', password: 'admin@123', email: 'adminOne@causecode.com'])

        SpringSecurityService springSecurityServiceForAdminOne = new SpringSecurityService()
        springSecurityServiceForAdminOne.metaClass.encodePassword = { String password -> 'ENCODED_PASSWORD' }
        adminOne.springSecurityService = springSecurityServiceForAdminOne
        assert adminOne.save(flush: true, failOnError: true)

        User adminTwo = new User([firstName: 'adminTwo', lastName: 'adminTwo', username: 'adminTwo', password: 'admin@123', email: 'adminTwo@causecode.com'])

        SpringSecurityService springSecurityServiceForAdminTwo = new SpringSecurityService()
        springSecurityServiceForAdminTwo.metaClass.encodePassword = { String password -> 'ENCODED_PASSWORD' }
        adminTwo.springSecurityService = springSecurityServiceForAdminTwo
        assert adminTwo.save(flush: true, failOnError: true)

        UserRole adminOneUserRole = UserRole.create(adminOne, adminRole, true)
        UserRole adminTwoUserRole = UserRole.create(adminTwo, adminRole, true)

        assert adminOneUserRole
        assert adminTwoUserRole
        assert UserRole.count() == 4

        when: 'removeAll method is called'
        UserRole.removeAll(adminRole)

        then: 'All UserRole instances having ROLE_ADMIN as Role must be deleted'
        UserRole.count() == 1
    }

    void 'test removeAll method when passed instance does not exist'() {
        when: 'removeAll method is called with non-existing user instance'
        User user = new User()
        UserRole.removeAll(user)

        then: 'No users must be deleted'
        UserRole.count() == 2

        when: 'removeAll method is called with non-existing role instance'
        Role role = new Role()
        UserRole.removeAll(role)

        then: 'No users must be deleted'
        UserRole.count() == 2
    }

    void 'test toString() method'() {
        when: 'UserRole instance is created and toString is called'
        String result = adminUserRole.toString()

        then:
        result == 'UserRole [1]'
    }

    User createAdminUser() {
        User newAdmin = new User([firstName: 'admin', lastName: 'admin', username: 'sysadmin', password: 'admin@123', email: 'sysadmin@causecode.com'])
        SpringSecurityService springSecurityServiceForAdminUser = new SpringSecurityService()
        springSecurityServiceForAdminUser.metaClass.encodePassword = { String password -> 'ENCODED_PASSWORD' }
        newAdmin.springSecurityService = springSecurityServiceForAdminUser
        assert newAdmin.save(flush: true, failOnError: true)
        return newAdmin
    }
}
