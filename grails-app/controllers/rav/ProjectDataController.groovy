package rav

import uk.co.desirableobjects.oauth.scribe.OauthService
import org.scribe.model.Token
import grails.converters.JSON


class ProjectDataController {

    def httpService
    def calculateService

    def index() { }
    OauthService oauthService // or new OauthService() would work if you're not in a spring-managed class.

    Token getToken() {
        String sessionKey = oauthService.findSessionKeyForAccessToken('ravelry');
        def sessionSessionKey = session[sessionKey];
        if (!sessionSessionKey){
            oauthService.getAccessToken()
        }
        return

    }

    def getUserData() {
        def ravelryAccessToken = getToken();
        def userName = params.userName;
        def allProjects = httpService.getProjects(userName,ravelryAccessToken);
        def stash = httpService.getStash(userName,ravelryAccessToken);

        def projectStats = calculateService.countProjectDetails(userName, allProjects.projects, ravelryAccessToken);
        def stashStats = calculateService.countStashDetails(userName,stash,ravelryAccessToken);

        def returnValues = ['projectStats':projectStats,'stashStas':stashStats];

        render returnValues as JSON
    }
}
