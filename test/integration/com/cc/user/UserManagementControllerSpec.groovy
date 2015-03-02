package com.cc.user



import grails.converters.JSON
import grails.test.spock.IntegrationSpec
import spock.lang.*
import org.springframework.http.HttpStatus;

class UserManagementControllerSpec extends GroovyTestCase {

    def userManagementService
    
    void testIndexAction() {
        def controller = new UserManagementController()
        controller.userManagementService = userManagementService
        
        when: "Index action is called"
        controller.index(15, 0, "Mysql")
        
        then: "List of users will be returned"
        println("Response is " + controller.response.json["instanceList"])
        controller.response.json["instanceList"] != null
        controller.response.json["totalCount"] != null
        controller.response.json["roleList"] != null
    }
    
}
