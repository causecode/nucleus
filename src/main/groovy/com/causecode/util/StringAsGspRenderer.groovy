package com.causecode.util

import grails.gsp.PageRenderer
import grails.util.Environment
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * A utility bean to compile & render a simple string text as a GSP content.
 * Basically, this bean creates a gsp template for given string (if not available) to compile & then
 * uses the groovy page renderer engine to render that template.
 * To use this bean just inject <code>def stringAsGspRenderer</code> in controller, domain
 * or services like other dependency injection.
 * @see NucleusGrailsPlugin.groovy for bean registration
 * @author Shashank Agrawal
 * @since v0.3.1
 */
@SuppressWarnings(['JavaIoPackageAccess'])
class StringAsGspRenderer {

    private static final Log logger = LogFactory.getLog(this)

    private static final String TEMPLATE_CACHE_DIRECTORY_NAME
    private static final String TEMPLATE_CACHE_DIRECTORY_PATH
    private static final String APP_ROOT

    private Map<String, String> pageTemplateURLCache = [:]

    // Groovy page template engine bean injected in NucleusGrailsPlugin.groovy
    PageRenderer groovyPageRenderer

    static {
        TEMPLATE_CACHE_DIRECTORY_NAME = 'template-cache'
        String catalinaHome = System.getenv('CATALINA_HOME')
        APP_ROOT = "$catalinaHome/webapps/ROOT/WEB-INF"

        // For supporting running of War using embedded container.
        TEMPLATE_CACHE_DIRECTORY_PATH = "$APP_ROOT/grails-app/views/$TEMPLATE_CACHE_DIRECTORY_NAME"

        if (!APP_ROOT || [Environment.DEVELOPMENT, Environment.TEST].contains(Environment.current)) {
            TEMPLATE_CACHE_DIRECTORY_PATH = "./grails-app/views/$TEMPLATE_CACHE_DIRECTORY_NAME"
        }

        File temporaryDirectoryPath = new File(TEMPLATE_CACHE_DIRECTORY_PATH)

        logger.debug "Temporary path for template directory: $temporaryDirectoryPath.absolutePath"

        if (!temporaryDirectoryPath.exists()) {
            boolean status = temporaryDirectoryPath.mkdirs()
            logger.debug "Created template cache directory with status: [$status]"
        }
    }

    void clearCache() {
        pageTemplateURLCache = [:]
    }

    /**
     * Used to cleanup all template cache created by this utility class at startup.
     */
    void cleanupTemplateCache() {
        logger.info 'cleanupTemplateCache'
    }

    /**
     * Used to cleanup the old version of any domain instance.
     * @param domainInstance The instance of any domain object with version field
     */
    void cleanupOldTemplate(Object domainInstance, String field) {
        if (!domainInstance || !domainInstance.id || !domainInstance.version) {
            logger.info "No older version to cleanup for $domainInstance"
            return
        }

        String pageID = getPageIdForDomain(domainInstance, field, true)
        File oldCacheFile = new File("$TEMPLATE_CACHE_DIRECTORY_PATH/_${pageID}.gsp")

        if (oldCacheFile.exists()) {
            logger.debug "Deleting old cache file: $oldCacheFile.absolutePath"
            oldCacheFile.delete()
        } else {
            logger.debug "Old cache file not exists: $oldCacheFile.absolutePath"
        }
    }

    /**
     * Used to generate a unique template id for a domain instance.
     * @param domainInstance The instance of any grails domain class to create id for.
     * @param field Field in the domain class for unique id
     * @param previousVersion Will create a id with lower version. Used to delete the older file.
     * @example User_14_2 for a instance of a domain class with id 14 & version 2.
     * If domain has not yet been persisted than current timestamp will be appended
     * to avoid the caching problem.
     */
    private String getPageIdForDomain(Object domainInstance, String field, boolean previousVersion = false) {
        StringBuilder pageID = new StringBuilder(domainInstance.class.simpleName.toLowerCase())
        pageID.append('_')
        pageID.append(field)
        pageID.append('_')

        if (domainInstance.id) {
            pageID.append(domainInstance.id.toString())
            pageID.append('_')
            if (previousVersion) {
                pageID.append((domainInstance.version - 1).toString())
            } else {
                pageID.append(domainInstance.version.toString())
            }
        } else {
            pageID.append(System.currentTimeMillis())
        }

        return pageID.toString()
    }

    void removeFromCache (Object domainInstance, String field) {
        removeFromCache(getPageIdForDomain(domainInstance, field))
    }

    /*
     * Used to remove a template from cache so new version of same template can be used.
     */
    void removeFromCache(String pageID) {
        pageTemplateURLCache.remove(pageID)
    }

    String render(String content, Map model) {
        render("cc${System.currentTimeMillis()}", content, model)
    }

    /**
     * Used to compile & render a given content binded with model as string.
     * @param pageId A unique pageId for the content to create a template.
     * @param content String content to compile & render as gsp.
     * @param model Model to be bind on the given content.
     * @return Compiled & converted string
     */
    String render(String pageID, String content, Map model) {
        clearCache()

        // Check if that template is already created & cached.
        String fileURL = pageTemplateURLCache.get(pageID)

        // If cached template URL not already exist
        if (!fileURL || !(new File(fileURL).exists())) {
            // Create the template path as in view folder
            File templateFile = new File("$TEMPLATE_CACHE_DIRECTORY_PATH/_${pageID}.gsp")
            // Write content to the file
            templateFile.text = content

            fileURL = templateFile.absolutePath
            pageTemplateURLCache.put(pageID, fileURL)
        }

        // Render content for new page.
        groovyPageRenderer.render([template: "/$TEMPLATE_CACHE_DIRECTORY_NAME/$pageID", model: model])
    }

    String renderFromDomain(Object domainInstance, String field, Map model) {
        render(getPageIdForDomain(domainInstance, field), domainInstance[field], model)
    }
}
