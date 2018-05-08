package com.causecode.user

import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * This class specifies unit test cases for {@link com.causecode.user.UserRole}.
 */
@Mock([User, Role, SpringSecurityService])
@TestFor(UserRole)
class UserRoleSpec extends Specification {

    User admin, normalUser
    Role adminRole, normalUserRole
    UserRole adminUserRole, userRole

    def setup() {
        admin = new User([firstName: 'admin', lastName: 'admin', username: 'admin', password: 'admin@123',
                          email    : 'admin@causecode.com'])

        mockedSpringSecurityService(admin)

        adminRole = new Role([authority: 'ROLE_ADMIN'])
        assert adminRole.save(flush: true, failOnError: true)

        adminUserRole = UserRole.create(admin, adminRole, true)
        assert adminUserRole

        normalUser = new User([firstName: 'normal', lastName: 'normal', username: 'normal', password: 'normal@123',
                               email    : 'normal@causecode.com'])

        mockedSpringSecurityService(normalUser)

        normalUserRole = new Role([authority: 'ROLE_USER'])
        assert normalUserRole.save(flush: true, failOnError: true)

        userRole = UserRole.create(normalUser, normalUserRole, true)
        assert userRole
    }

    void mockedSpringSecurityService(User userInstance) {
        SpringSecurityService securityServiceMock = Mock(SpringSecurityService)
        securityServiceMock.encodePassword(_) >> 'ENCODED_PASSWORD'
        userInstance.springSecurityService = securityServiceMock
        assert userInstance.save(flush: true, failOnError: true)
    }

    @Unroll
    void 'test equals method when passed instance is #passedAdminUserInstance'() {
        when: 'equals method is called'
        boolean result
        switch (passedAdminUserRole) {
            case 'adminRole':
                result = adminUserRole == adminRole
                break

            case 'normalUserRole':
                result = adminUserRole == normalUserRole
                break

            case 'localAdminUserRole':
                UserRole localAdminUserRole = adminUserRole
                result = adminUserRole == localAdminUserRole
                break

            case 'userRole':
                userRole = UserRole.create(admin, adminRole, true)
                result = (adminUserRole == (userRole))
                break

            case 'null':  result = adminUserRole == null
                break
        }

        then: 'equals method must return false'
        result == expectedResult

        where: 'The given values are following'
        passedAdminUserRole  | expectedResult
        'adminRole'          | false
        'normalUserRole'     | false
        'localAdminUserRole' | true
        'userRole'           | true
        'null'               | false
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

    @Unroll
    void 'test get(long userId, long roleId) method when userId is #userId and roleId is #roleId'() {
        when: 'get(long userId, long roleId) method is called'
        UserRole userRole = UserRole.get(userId ? admin.id : 0L, roleId ? adminRole.id : 0L)

        then: 'Returned instance must match with the instance created above'
        userRole == (returnedUserRole ? adminUserRole : null)

        where: 'The given values are following'
        userId | roleId | returnedUserRole
        true   | true   | true
        false  | false  | false
    }

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

        savedInstance == newAdminUserRole
        UserRole.count() == userRoleCount + 1
    }

    void 'test create method when role is not passed at all'() {
        given: 'User instance'
        int userRoleCount = UserRole.count()
        assert userRoleCount == 2
        User newAdmin = createAdminUser()

        when: 'Create method is called'
        UserRole createdUserRole = UserRole.create(newAdmin)
        assert createdUserRole

        then: 'UserRole will be created with role ROLE_USER'
        Role role = Role.findByAuthority('ROLE_USER')
        UserRole savedInstance = UserRole.get(newAdmin.id, role.id)
        savedInstance == createdUserRole
        UserRole.count() == userRoleCount + 1
    }

    // Suppressed warning since, the get method is called with the closure, it will reduce code readability.
    @SuppressWarnings(['EmptyLineAfterClosingBraceRule'])
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
        savedInstance == createdUserRole
        UserRole.count() == userRoleCount + 1
        assert newAdmin.authorities[0] == newAdminRole
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

    void 'test removeAll method to delete all user roles by passing role instance'() {
        given: 'UserRole instance for two users'
        User adminOne = new User([firstName: 'adminOne', lastName: 'adminOne', username: 'adminOne',
                password: 'admin@123', email: 'adminOne@causecode.com'])

        User adminTwo = new User([firstName: 'adminTwo', lastName: 'adminTwo', username: 'adminTwo',
                password: 'admin@123', email: 'adminTwo@causecode.com'])

        and: 'Mocked SpringSecurityService for encodePassword and save instance'
        mockedSpringSecurityService(adminOne)
        mockedSpringSecurityService(adminTwo)

        and: 'Create userRoles'
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

    @Unroll
    void 'test removeAll method when passed instance does not exist'() {
        when: 'removeAll method is called with non-existing user instance'
        UserRole.removeAll(givenUser)

        then: 'No users must be deleted'
        UserRole.count() == 2

        where: 'The given values are following'
        givenUser << [new User(), new Role()]
    }

    void 'test toString() method'() {
        when: 'UserRole instance is created and toString is called'
        String result = adminUserRole

        then: 'UserRole will be returned as String'
        result == 'UserRole(1)'
    }

    User createAdminUser() {
        User newAdmin = new User([firstName: 'admin', lastName: 'admin', username: 'sysadmin', password: 'admin@123',
                email: 'sysadmin@causecode.com'])

        mockedSpringSecurityService(newAdmin)

        return newAdmin
    }
}
