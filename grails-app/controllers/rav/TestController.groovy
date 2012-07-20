package rav

import org.scribe.model.Token;
import uk.co.desirableobjects.oauth.scribe.OauthService;

class TestController {

    OauthService oauthService // or new OauthService() would work if you're not in a spring-managed class.

    Token getToken() {

        String sessionKey = oauthService.findSessionKeyForAccessToken('ravelry')
        return session[sessionKey]

    }

    def success() {
        def token = getToken();
        [token: token.toString()]
    }

    def error() {
        [test: error]
    }
}

