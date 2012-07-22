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
        "/getUserData"(controller: "testController", action: "getUserData")
		"500"(view:'/error')
	}
}
