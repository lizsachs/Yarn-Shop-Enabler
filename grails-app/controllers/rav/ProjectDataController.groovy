package rav

import uk.co.desirableobjects.oauth.scribe.OauthService
import org.scribe.model.Token
import grails.converters.JSON


class ProjectDataController {

    HttpService httpService
    def calculateService

    OauthService oauthService // or new OauthService() would work if you're not in a spring-managed class.

    Token getToken() {
        String sessionKey = oauthService.findSessionKeyForAccessToken('ravelry')
        def token = session[sessionKey];
        if (!token){
            throw new HttpService.AuthenticationException();
        }
        return token
    }

    def getUserData() {
        def returnValues = [:];

        try{
            def ravelryAccessToken = getToken();
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
        }
        catch(HttpService.AuthenticationException e){
            flash.message = "Your authentication has timed out. Please log in again."
            returnValues = ['error':['errorURL':grailsApplication.config.grails.serverURL,'errorCode':'authenticationError']];
        }
        render returnValues as JSON

    }
}
