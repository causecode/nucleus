package com.causecode.user

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.*


@TestFor(UserManagementService)
@Mock([User, Role, UserRole])
class UserManagementServiceSpec extends Specification {

    User adminUser
    User normalUser
    User managerUser
    Role userRole
    def springSecurityServiceForAdminUser
    def springSecurityServiceForNormalUser
    def springSecurityServiceForManagerUser

    def setup() {

        adminUser = new User([username : "admin", password: "admin@13", email: "bootstrap@causecode.com",
                              firstName: "CauseCode", lastName: "Technologies", gender: "male", enabled: true])
        springSecurityServiceForAdminUser = new Object()
        springSecurityServiceForAdminUser.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        adminUser.springSecurityService = springSecurityServiceForAdminUser
        assert adminUser.save(flush: true)

        normalUser = new User([username : "normalUser", password: "normalUser@132", email: "normalUserbootstrap@causecode.com",
                               firstName: "normalUserCauseCode", lastName: "normalUserTechnologies", gender: "male", enabled: true])
        springSecurityServiceForNormalUser = new Object()
        springSecurityServiceForNormalUser.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        normalUser.springSecurityService = springSecurityServiceForNormalUser
        assert normalUser.save(flush: true)

        managerUser = new User([username : "managerUser", password: "managerUser@134", email: "managerUserbootstrap@causecode.com",
                                firstName: "managerUserCauseCode", lastName: "managerUserTechnologies", gender: "male", enabled: true])
        springSecurityServiceForManagerUser = new Object()
        springSecurityServiceForManagerUser.metaClass.encodePassword = { String password -> "ENCODED_PASSWORD" }
        managerUser.springSecurityService = springSecurityServiceForManagerUser
        assert managerUser.save(flush: true)

        userRole = Role.findOrSaveByAuthority("ROLE_USER")
        assert userRole.save(flush: true)
    }

    void "test listForMysql with params"() {
        given:
        Map params = [
                offset    : 0,
                max       : 15,
                roleFilter: [userRole.id.toString()],
                roleType  : "Any Granted"
        ]

        UserRole.metaClass.'static'.executeQuery = { String query, Map stringQueryParams, Map params1 ->
            return ["adminUser", "normalUser"]
        }

        when: "listForMySql method called"
        Map result = service.listForMysql(params)

        then: "List of users will be returned"
        assert !result.isEmpty()
    }

    void "test getAppropriatedList with params"() {
        given:
        List ids = [adminUser.id, normalUser.id, managerUser.id] //["adminUser"] Fails

        when: "getAppropriatedList method called"
        List result = service.getAppropiateIdList(ids)

        then: "List of users will be returned"
        assert !result.isEmpty()
    }

    void "test getList with params"() {
        given:
        Map params = [
                offset    : 0,
                max       : 15,
                roleFilter: [userRole.id.toString()],
                roleType  : "Any Granted"
        ]

        List userInstanceList = [adminUser, normalUser, managerUser]

        service.metaClass.listForMysql = { Map params1 ->
            return [instanceList: userInstanceList, totalCount: userInstanceList.size()]
        }

        when: "getList method called"
        Map result = service.getList(params)

        then: "List of users will be returned"
        assert !result.isEmpty()
    }

    void "test getSelectedItemList with arguments"() {
        given:
        String selectedIds = [adminUser.id.toString(), normalUser.id.toString(), managerUser.id.toString()]
        boolean selectAll = true
        Map args = [
                offset    : 0,
                max       : 15,
                roleFilter: [userRole.id.toString()],
                roleType  : ""//"Any Granted"
        ]

        UserRole.metaClass.'static'.executeQuery = { String query, Map stringQueryParams, Map params1 ->
            return ["adminUser", "normalUser", "managerUser"]
        }

        //getSelectedItemList(boolean selectAll, String selectedIds, Map args)
        when: "getAppropriatedList method called"
        List result = service.getSelectedItemList(selectAll, selectedIds, args)

        then: "List of users will be returned"
        assert !result.isEmpty()
    }

}
