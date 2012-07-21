package rav

import org.scribe.model.Token;
import uk.co.desirableobjects.oauth.scribe.OauthService;
import groovy.json.*

class testController {

    OauthService oauthService // or new OauthService() would work if you're not in a spring-managed class.

    Token getToken() {
        String sessionKey = oauthService.findSessionKeyForAccessToken('ravelry')
        return session[sessionKey]

    }

    def testToken() {
        def token = getToken();
        [token: token.toString()]
    }

    def testHttpGet() {
        def ravelryAccessToken = getToken();
        def response = oauthService.getRavelryResource(ravelryAccessToken, 'https://api.ravelry.com/projects/' + 'blacktabi' + '/list.json');
        def responseJson = new JsonSlurper().parseText(response.getBody())

        render(view: "testToken", model: [projects: responseJson.projects])
    }
}