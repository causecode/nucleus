/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.util

import grails.gsp.PageRenderer
import grails.util.Environment

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * A utility bean to compile & render a simple string text as a GSP content.
 * Basically, this bean creates a gsp template for given string (if not available) to compile & then 
 * uses the groovy page renderer engine to render that template.
 * 
 * To use this bean just inject <code>def stringAsGspRenderer</code> in controller, domain
 * or services like other dependency injection.
 * @see NucleusGrailsPlugin.groovy for bean registration
 * @author Shashank Agrawal
 * @since v0.3.1
 *
 */
class StringAsGspRenderer {

    private static Log log = LogFactory.getLog(this)

    private static final String TEMPLATE_CACHE_DIRECTORY_NAME
    private static final String TEMPLATE_CACHE_DIRECTORY_PATH

    // Groovy page template engine bean injected in NucleusGrailsPlugin.groovy
    PageRenderer groovyPageRenderer

    private Map<String, String> pageTemplateURLCache = new HashMap<String, String>()

    static {
        TEMPLATE_CACHE_DIRECTORY_NAME = "/template-cache"

        TEMPLATE_CACHE_DIRECTORY_PATH = "./grails-app/views/$TEMPLATE_CACHE_DIRECTORY_NAME"

        if (![Environment.DEVELOPMENT, Environment.TEST].contains(Environment.current)) {
            String catalinaHome = System.getenv("CATALINA_HOME")
            TEMPLATE_CACHE_DIRECTORY_PATH = "$catalinaHome/webapps/ROOT/WEB-INF/$TEMPLATE_CACHE_DIRECTORY_PATH"
        }

        File temporaryDirectoryPath = new File(TEMPLATE_CACHE_DIRECTORY_PATH)

        log.debug "Temporary path for template directory: $temporaryDirectoryPath.absolutePath"

        if (!temporaryDirectoryPath.exists()) {
            boolean status = temporaryDirectoryPath.mkdirs()
            log.debug "Created template cache directory with status: [$status]"
        }
    }

    void clearCache() {
        pageTemplateURLCache = [:]
    }

    /**
     * Used to generate a unique template id for a domain instance.
     * @example User_14 for a instance of a domain class with id 14.
     */
    private String getPageIdForDomainInstance(Object domainInstance) {
        StringBuilder pageId = new StringBuilder(domainInstance.class.simpleName.toLowerCase())
        pageId.append("_")
        pageId.append(domainInstance.id?.toString())
        pageId.append("_")
        pageId.append(domainInstance.version?.toString())

        pageId.toString()
    }

    void removeFromCache(Object domainInstance){
        removeFromCache(getPageIdForDomainInstance(domainInstance))
    }

    /*
     * Used to remove a template from cache so new version of same template can be used.
     */
    void removeFromCache(String pageId){
        pageTemplateURLCache.remove(pageId)
    }

    String render(Object domainInstance, String content, Map model) {
        render(getPageIdForDomainInstance(domainInstance), content, model)
    }

    /**
     * Used to compile & render a given content binded with model as string.
     * 
     * @param pageId A unique pageId for the content to create a template.
     * @param content String content to compile & render as gsp.
     * @param model Model to be bind on the given content.
     * @return Compiled & converted string
     */
    String render(String pageId, String content, Map model) {
        clearCache()

        // Check if that template is already created & cached.
        String fileURL = pageTemplateURLCache.get(pageId)

        if (!fileURL || !(new File(fileURL).exists())) {
            // Create the template path as in view folder
            File templateFile = new File("$TEMPLATE_CACHE_DIRECTORY_PATH/_${pageId}.gsp")
            // Write content to the file
            templateFile << content

            fileURL = templateFile.absolutePath
            pageTemplateURLCache.put(pageId, fileURL)
        }

        // Render content for new page.
        groovyPageRenderer.render([template: "/$TEMPLATE_CACHE_DIRECTORY_NAME/$pageId", model: model])
    }
}