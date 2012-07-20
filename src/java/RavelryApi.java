import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

/**
 * Created with IntelliJ IDEA.
 * User: Liz
 * Date: 7/18/12
 * Time: 9:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class RavelryApi extends DefaultApi10a
{
    private static final String AUTHORIZE_URL = "https://www.ravelry.com/oauth/authorize?oauth_token=%s";
    @Override
    public String getAccessTokenEndpoint()
    {
        return "https://www.ravelry.com/oauth/access_token";
    }
    @Override
    public String getRequestTokenEndpoint()
    {
        return "https://www.ravelry.com/oauth/request_token";
    }

    @Override
    public String getAuthorizationUrl(Token requestToken)
    {
        return String.format(AUTHORIZE_URL, requestToken.getToken());
    }

}
