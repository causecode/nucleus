package nucleus

/**
 * Defines url mappings for userManagement and sitemap.xml in Nucleus plugin.
 */
class NucleusUrlMappings {

    static mappings = {

        /**
         * This mapping is used to replicate grails rest default mapping. Request data can
         * be passed as request body or as request URL depending on type of operation.
         * Like, create & update operation must use request body to send parameters with
         * either POST or PUT requests.
         *
         * @see 'http://grails.org/doc/latest/guide/single.html#restfulMappings'
         *
         * @example
         *
         *      GET     '/api/v1/user' will call index action of UserController
         *      POST    '/api/v1/user' will call save action of UserController
         *      PUT     '/api/v1/user/2' will call update action of UserController with id 2
         *      DELETE  '/api/v1/user/2' will call update action of UserController with id 2
         *      GET     '/api/v1/user/2' will call show action of UserController with id 2
         *      GET     '/api/v1/user/action/autocomplete' will call autocomplete action of UserController with null id
         *      GET     '/api/v1/user/action/followUp will call followUp action of UserController
         */
        "/api/v1/$controller/$resourceId?/$customAction?" {
            namespace = 'v1'
            action = {
                Map actionMethodMap = [GET: params.resourceId ? 'show' : 'index', POST: 'save', PUT: 'update',
                                       DELETE: 'delete']

                return params.customAction ?: actionMethodMap[request.method.toUpperCase()]
            }
            id = {
                if (params.resourceId == 'action') {
                    return params.id
                }
                return params.resourceId
            }
        }

        '/userManagement' (resources: 'user')
        '/sitemap.xml'(controller: 'sitemap', action: 'index')
    }
}
