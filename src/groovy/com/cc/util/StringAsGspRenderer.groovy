/*
 * Copyright (c) 2011-Present, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.util

import groovy.text.Template

import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine

/**
 * A utility bean to compile & render a simple string text as a GSP content.
 * Basically, this bean creates a template for given string to compile & then 
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

    // Groovy page template engine bean injected in NucleusGrailsPlugin.groovy
    GroovyPagesTemplateEngine groovyPagesTemplateEngine

    private Map<String, Template> pageTemplateCache = new HashMap<String, Template>()

    /**
     * Used to generate a unique template id for a domain instance.
     * @example User_14 for a instance of a domain class with id 14.
     */
    private String getPageIdForDomainInstance(Object domainInstance) {
        StringBuilder pageId = new StringBuilder(domainInstance.class.simpleName)
        pageId.append("_")
        pageId.append(domainInstance.id?.toString())

        pageId.toString()
    }

    void removeFromCache(Object domainInstance){
        removeFromCache(getPageIdForDomainInstance(domainInstance))
    }

    /*
     * Used to remove a template from cache so new version of same template can be used.
     */
    void removeFromCache(String pageId){
        pageTemplateCache.remove(pageId)
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
        StringWriter stringWriterInstance = new StringWriter()

        // Check if that template is already created & cached.
        Template template = pageTemplateCache.get(pageId)

        if (!template) {
            // If not create a template and store it to the cache. (It doesn't actually creates a file.)
            template = groovyPagesTemplateEngine.createTemplate(content, "${pageId}.gsp")
            pageTemplateCache.put(pageId, template)
        }

        // Apply the given model to the content template & write it to the writere.
        template.make(model).writeTo(stringWriterInstance)

        stringWriterInstance.toString()
    }
}