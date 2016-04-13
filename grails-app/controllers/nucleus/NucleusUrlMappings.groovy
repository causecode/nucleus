package nucleus

class NucleusUrlMappings {

    static mappings = {

        "/userManagement" (resources: "user")
        "/sitemap.xml"(controller: "sitemap", action: "index")
    }
}
