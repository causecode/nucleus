package nucleus

/**
 * Defines url mappings for userManagement and sitemap.xml in Nucleus plugin.
 */
class NucleusUrlMappings {

    static mappings = {

        '/userManagement' (resources: 'user')
        '/sitemap.xml'(controller: 'sitemap', action: 'index')
    }
}
