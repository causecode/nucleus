package nucleus

class UrlMappings {

    static mappings = {

        "/" (uri: "/ng/app/index.html")

        "/api/$controller/$resourceId?/$customAction?" {
            action = {
                Map actionMethodMap = [GET: params.resourceId ? "show" : "index", POST: "save", PUT: "update", DELETE: "delete"]

                return params.customAction ?: actionMethodMap[request.method.toUpperCase()]
            }
            id = {
                if (params.resourceId == "action") {
                    return params.id
                }
                return params.resourceId
            }
        }
    }
}