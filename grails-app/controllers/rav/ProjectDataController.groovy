package rav

import uk.co.desirableobjects.oauth.scribe.OauthService
import org.scribe.model.Token
import grails.converters.JSON


class ProjectDataController {

    HttpService httpService
    def calculateService

    def index() { }
    OauthService oauthService // or new OauthService() would work if you're not in a spring-managed class.

    Token getToken() {
        String sessionKey = oauthService.findSessionKeyForAccessToken('ravelry')
        return session[sessionKey];
    }

    def getUserData() {
        def ravelryAccessToken = getToken();
        def returnValues = [:];

        if (ravelryAccessToken){
            def userName = params.userName;
            def allProjects = httpService.getProjects(userName,ravelryAccessToken);
            //def stash = httpService.getStash(userName,ravelryAccessToken);
            def projectStats;
            def stashStats;

            if (allProjects != null && allProjects.projects.size() > 0){
                projectStats = calculateService.countProjectDetails(userName, allProjects.projects, ravelryAccessToken);
            }
            else{
                projectStats = ['message':"This user has no project data."];
            }

            if (false){
                stashStats = calculateService.countStashDetails(userName,stash,ravelryAccessToken);
            }
            else{
                stashStats = ['message':"This user has no stash data."];
            }

            returnValues = ['projectStats':projectStats,'stashStas':stashStats];

        } else {
            returnValues = ['error':['errorURL':grailsApplication.config.grails.serverURL,'errorCode':'authenticationError']];
        }
        render returnValues as JSON

    }
}
