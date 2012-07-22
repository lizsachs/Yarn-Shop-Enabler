class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
        "/error.gsp"(view:'/error')
        "/success.gsp"(view:'/success')
        "/testToken.gsp"(view:'/projectData/testToken')
        "/getUserData"(controller: "testController", action: "getUserData")
		"500"(view:'/error')
	}
}
