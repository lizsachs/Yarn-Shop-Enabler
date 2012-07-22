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
        String sessionKey = oauthService.findSessionKeyForAccessToken('ravelry')
        if (session[sessionKey]){
            return session[sessionKey];
        }
        else{
            redirect(uri:  "rav");
        }
    }

    def getUserData() {
        def ravelryAccessToken = getToken();
        def userName = params.userName;
        def allProjects = httpService.getProjects(userName,ravelryAccessToken);
        def stash = httpService.getStash(userName,ravelryAccessToken);
        def projectStats;
        def stashStats;

        if (allProjects.projects.size > 0){
            projectStats = calculateService.countProjectDetails(userName, allProjects.projects, ravelryAccessToken);
        }
        else{
            projectStats = ['message':"This user has no project data."];
        }

        if (stash.stash.size > 0){
            stashStats = calculateService.countStashDetails(userName,stash,ravelryAccessToken);
        }
        else{
            stashStats = ['message':"This user has no stash data."];
        }

        def returnValues = ['projectStats':projectStats,'stashStas':stashStats];

        render returnValues as JSON
    }
}
