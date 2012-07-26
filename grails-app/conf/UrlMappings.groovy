class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
        "/error.gsp"(view:'/error')
        "/enable.gsp"(view:'/enable')
        "/testToken.gsp"(view:'/projectData/testToken')
        "/getProjectStats"(controller: "projectData", action: "getProjectStats")
		"500"(view:'/error')
	}
}
