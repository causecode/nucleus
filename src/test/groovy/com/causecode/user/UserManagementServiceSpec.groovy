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
    Role adminRole
    def springSecurityServiceForAdminUser
    def springSecurityServiceForNormalUser
    def springSecurityServiceForManagerUser

    def setup() {

        adminUser = new User([username : "admin", password: "admin@13", email: "bootstrap@causecode.com",
                              firstName: "adminCausecode", lastName: "adminCausecode", gender: "male", enabled: true])
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

        adminRole = Role.findOrSaveByAuthority('ROLE_ADMIN')
        userRole = Role.findOrSaveByAuthority('ROLE_USER')
    }

    void "test listForMysql with params roleType anyGranted"() {
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

    void "test listForMysql with params roleType allGranted"() {
        given:
        Map params = [
                offset    : 0,
                max       : 15,
                roleFilter: [userRole.id.toString()],
                roleType  : "All Granted"
        ]

        UserRole.metaClass.'static'.executeQuery = { String query, Map stringQueryParams, Map params1 ->
            return ["adminUser", "normalUser"]
        }

        when: "listForMySql method called"
        Map result = service.listForMysql(params)

        then: "List of users will be returned"
        assert !result.isEmpty()
    }

/*
    void 'test getList with mongo as parameter'() {
        given:
        Map params = [
                offset    : 0,
                max       : 15,
                dbType    : 'Mongo',
                letter    : 'a',
                query     : 'adm',
                roleFilter: [adminRole.id.toString()],
                roleType  : 'Any Granted'
        ]
        when: 'getList method is called'
        Map result = service.getList(params)

        then: 'Result map is returned'
        result['totalCount'] == 1
        result['instanceList'].get(0) == adminUser
    }

    void 'test getList with mongo when roleFilter is passed as String'() {
        given:
        Map params = [
                offset    : 0,
                max       : 15,
                dbType    : 'Mongo',
                roleFilter: adminRole.id.toString(),
                roleType  : 'All Granted'
        ]
        when: 'getList method is called'
        Map result = service.getList(params)

        then: 'Result map is returned'
        result['totalCount'] == 1
        result['instanceList'].get(0) == adminUser
    }

    void 'test getList with mongo when roleType is not passed at all'() {
        given:
        Map params = [
                offset    : 0,
                max       : 15,
                dbType    : 'Mongo',
                roleFilter: adminRole.id.toString(),
        ]

        when: 'getList method is called'
        Map result = service.getList(params)

        then: 'Result map is returned'
        result['totalCount'] == 0
        result['instanceList'].size() == 0
    }

    void 'test getList when roleFilter is not passed at all'() {
        given:
        Map params = [
                offset: 0,
                max   : 15,
                letter: 'a',
                query : 'adm',
                dbType: 'Mongo'
        ]

        when: 'getList method is called'
        Map result = service.getList(params)

        then: 'Result map is returned'
        result['totalCount'] == 1
        result['instanceList'].get(0) == adminUser
    }
*/

    void "test getAppropriatedList with params"() {
        given:
        List ids = [adminUser.id, normalUser.id, managerUser.id] //["adminUser"] Fails

        when: "getAppropriatedList method called"
        List result = service.getAppropiateIdList(ids)

        then: "List of users will be returned"
        assert !result.isEmpty()
    }

    void "test getAppropriatedList with null list ids"() {
        given:
        List ids = null

        when: "getAppropriatedList method called"
        List result = service.getAppropiateIdList(ids)

        then: "Empty list will be returned"
        assert result.isEmpty()
    }

    void 'test getAppropriatedList with ids for mongo'() {
        given:
        List ids = ['507f191e810c19729de860ea', '4cdfb11e1f3c000000007822', '4cdfb11e1f3c000000007822']

        when: 'getAppropriatedList method is called'
        List result = service.getAppropiateIdList(ids)

        then: 'List with unique ids is returned'
        assert !result.isEmpty()
        assert result.size() == 2
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

    void "test getList with letter and query in params"() {
        given: 'Params map'
        Map params = [
                offset: 0,
                max   : 15,
                letter: 'a'
        ]
        assert UserRole.create(normalUser, userRole, true)
        assert UserRole.create(managerUser, userRole, true)
        assert UserRole.create(adminUser, adminRole, true)

        UserRole.metaClass.'static'.executeQuery = { String query, Map stringQueryParams, Map args ->
            return [adminUser]
        }

        when: "getList method called with letter in params"
        Map result = service.getList(params)

        then: "List of users will be returned"
        assert !result.isEmpty()
        result['totalCount'] == 1
        result['instanceList'].get(0) == adminUser

        when: 'getList method called with query in params '
        params['letter'] = ''
        params['query'] = 'adm'
        result = null
        result = service.getList(params)

        then: 'List of users matching the criteria will be returned'
        assert !result.isEmpty()
        result['totalCount'] == 1
        result['instanceList'].get(0) == adminUser
    }

    void "test getSelectedItemList with arguments"() {
        given:
        String selectedIds = adminUser.id + "," + normalUser.id + "," + managerUser.id
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
        List result = service.getSelectedItemList(true, selectedIds, args)

        then: "List of users will be returned"
        assert !result.isEmpty()

        when: 'getSelectedItemList method is called'
        result = service.getSelectedItemList(false, selectedIds, args)

        then: 'All users with provided ids will be returned as result'
        assert !result.isEmpty()
        result.get(0).id == adminUser.id
        result.get(1).id == normalUser.id
        result.get(2).id == managerUser.id

        when: 'getSelectedItemList method is called with selectAll= false and selectedIds as empty'
        result = service.getSelectedItemList(false, '', args)

        then: 'Empty list is returned'
        assert result.isEmpty()
    }
}
