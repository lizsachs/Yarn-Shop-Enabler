import uk.co.desirableobjects.oauth.scribe.OauthService;
import org.scribe.model.Token;

class testService {

    OauthService oauthService

    def testOath() {
        Token ravelryAccessToken = session[oauthService.findSessionKeyForAccessToken('ravelry')]
        oauthService.getOauthResourceService(ravelryAccessToken, 'http://api.ravelry.com/users/list')
    }
}