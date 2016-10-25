/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.util

import com.causecode.user.User
import grails.gsp.PageRenderer
import grails.test.mixin.Mock
import org.apache.commons.logging.Log
import spock.lang.Specification
import spock.util.mop.ConfineMetaClassChanges

@Mock(User)
class StringAsGspRendererSpec extends Specification{
    User adminUser
    StringAsGspRenderer stringAsGspRenderer
    def setup() {
        adminUser = new User([username : "dummy1", password: "dummy@13", email: "dummy@something.com",
                              firstName: "Dummy", lastName: "User", gender: "male"])
        def springSecurityServiceForAdminUser = new Object()
        springSecurityServiceForAdminUser.metaClass.encodePassword = { String password -> 'ENCODED_PASSWORD' }
        adminUser.springSecurityService = springSecurityServiceForAdminUser
        assert adminUser.save(flush: true, failOnError: true)
        assert adminUser.id != null

        stringAsGspRenderer = new StringAsGspRenderer()
    }

    void 'test clearCache method'() {
        when: 'clearCache method is called'
        stringAsGspRenderer.clearCache()

        then: 'pageTemplatUrlCache must get cleared'
        stringAsGspRenderer.pageTemplateURLCache.isEmpty()
    }

    void 'test cleanupOldTemplate method'() {
        given: 'Mocked Logger'
        String logStatement
        StringAsGspRenderer.logger = [debug: { Object message ->
            logStatement = message
        }, info: { Object message ->
            logStatement = message
        }, error: { Object message, Throwable e = new Exception() ->
            logStatement = message
            println e.message
        } ] as Log

        when: 'cleanupOldTemplate method is called and domain instance is null'
        stringAsGspRenderer.cleanupOldTemplate(null,'name')

        then: 'method returns without performing any task'
        logStatement.contains('No older version to cleanup for')

        when: 'cleanupOldTemplate method is called and older cache file does not exist'
        adminUser.version = 1
        String path = stringAsGspRenderer.TEMPLATE_CACHE_DIRECTORY_PATH
        stringAsGspRenderer.cleanupOldTemplate(adminUser,'name')
        File oldCacheFile = new File("$path/_user_name_1_0.gsp")
        oldCacheFile.delete()

        then: 'if oldCache file existed earlier then it is deleted'
        !oldCacheFile.exists()

        when: 'cleanupOldTemplate method is called and older cache file exists'
        adminUser.version = 1
        path = stringAsGspRenderer.TEMPLATE_CACHE_DIRECTORY_PATH
        oldCacheFile = new File("$path/_user_name_1_0.gsp")
        try {
            oldCacheFile.createNewFile()
        } catch (IOException e) {
            e.printStackTrace()
        }
        stringAsGspRenderer.cleanupOldTemplate(adminUser,'name')

        then: 'if oldCache file existed earlier then it is deleted'
        !oldCacheFile.exists()
    }

    void 'test getPageIdForDomain method'() {
        when:'getPageIdForDomain method is called and previous version flag is false'
        String pageId = stringAsGspRenderer.getPageIdForDomain(adminUser,'username',false)

        then:'returned pageId must match the expected pageId'
        pageId == 'user_username_1_0'

        when:'getPageIdForDomain method is called and previous version flag is true'
        adminUser.version = 1
        pageId = stringAsGspRenderer.getPageIdForDomain(adminUser,'username',true)

        then:'returned pageId must match the expected pageId'
        pageId.contains('user_username_1_0')


        when:'getPageIdForDomain method is called and instance does not have id field'
        adminUser.id = null
        pageId = stringAsGspRenderer.getPageIdForDomain(adminUser,'username',true)

        then:'returned pageId must match the expected pageId'
        pageId.contains('user_username_')
    }

    void 'test removeFromCache method'() {
        when:'removeFromCache method is called with String as an argument'
        stringAsGspRenderer.pageTemplateURLCache = ['user_username_1_0' : '/template-cache/_user_name_1_0.gsp']
        assert !stringAsGspRenderer.pageTemplateURLCache.isEmpty()

        stringAsGspRenderer.removeFromCache('user_username_1_0')

        then:'pageID must be removed from the map'
        stringAsGspRenderer.pageTemplateURLCache.isEmpty()

        when: 'removeFromCache method is called with object as an argument'
        stringAsGspRenderer.pageTemplateURLCache = ['user_username_1_0' : '/template-cache/_user_name_1_0.gsp']
        assert !stringAsGspRenderer.pageTemplateURLCache.isEmpty()

        stringAsGspRenderer.removeFromCache(adminUser,'username')

        then: 'pageID relative to the instance must be removed from the map'
        stringAsGspRenderer.pageTemplateURLCache.isEmpty()

    }

    @ConfineMetaClassChanges(PageRenderer)
    void 'test render method'() {
        given: 'PageTemplateCacheMap with required entries'
        assert stringAsGspRenderer.pageTemplateURLCache.size() == 0
        PageRenderer renderer = new PageRenderer()
        renderer.metaClass.render = { Map params ->
            return 'Page Rendered'
        }
        stringAsGspRenderer.groovyPageRenderer = renderer

        when: 'render method is called'
        stringAsGspRenderer.render('content',['username' : 'causecode'])

        then: 'Following condition must be satisfied'
        stringAsGspRenderer.pageTemplateURLCache.size() == 1
    }

    @ConfineMetaClassChanges(PageRenderer)
    void 'test renderFromDomain method'() {
        given: 'user instance and pageID'
        adminUser.version = 1
        String pageID = 'user_username_1_1'
        assert stringAsGspRenderer.pageTemplateURLCache.size() == 0

        PageRenderer renderer = new PageRenderer()
        renderer.metaClass.render = { Map params ->
            return 'Page Rendered'
        }
        stringAsGspRenderer.groovyPageRenderer = renderer

        when: 'renderFromDomain method is called'
        stringAsGspRenderer.renderFromDomain(adminUser,'username', ['username' : 'causecode'])

        then: 'Following conditions must be satisfied'
        stringAsGspRenderer.pageTemplateURLCache.size() == 1
        stringAsGspRenderer.pageTemplateURLCache.containsKey(pageID)
    }

    void 'test cleanupTemplateCache method'() {
        given: 'Mocked Logger'
        String logStatement
        StringAsGspRenderer.logger = [debug: { Object message ->
            logStatement = message
        }, info: { Object message ->
            logStatement = message
        }, error: { Object message, Throwable e = new Exception() ->
            logStatement = message
            println e.message
        } ] as Log

        when: 'cleanupTemplateCache method is called'
        stringAsGspRenderer.cleanupTemplateCache()

        then: 'Nothing happens'
        logStatement == 'cleanupTemplateCache'
    }
}
