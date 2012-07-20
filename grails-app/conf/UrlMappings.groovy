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
		"500"(view:'/error')
	}
}
