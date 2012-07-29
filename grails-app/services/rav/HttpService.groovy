package rav

import uk.co.desirableobjects.oauth.scribe.OauthService;
import org.scribe.model.Token
import groovy.json.JsonSlurper;


class HttpService {
    public static class AuthenticationException extends Exception{}

    OauthService oauthService

    def getResource(String url,Token ravToken) throws AuthenticationException{
        def response = oauthService.getRavelryResource(ravToken,url);
        def jsonResponse = null;
        if(response.isSuccessful()){
            jsonResponse = new JsonSlurper().parseText(response.getBody())
        }
        else{
            if(response.headers.Status == '401'){
                throw new AuthenticationException();
            }
            else if(response.headers.Status == '500'){
                //do nothing if status is 500.  Ravely throws a 500 error occasionally for specific projects, but we still want to calculate the other project data without erroring out
                println("500 Error: " + response.headers);
                jsonResponse = null;
            }
            else{
                throw new Exception();
            }
        }
        return jsonResponse;
    }

    def getProjects(String userName, Token ravToken) throws AuthenticationException{
        def allProjects = getResource('https://api.ravelry.com/projects/' + userName + '/list.json',ravToken);
        return allProjects;
    }

    def getProjectDetails(String userName, int projectId, Token ravToken)   {
        def projectDetails = getResource('https://api.ravelry.com/projects/' + userName + '/' + projectId + '.json',ravToken)
        return projectDetails;
    }

    def getYarnDetails(int yarnId, Token ravToken){
        def yarnDetails = getResource('https://api.ravelry.com/yarns/'+ yarnId + '.json',ravToken);
        return yarnDetails;
    }

    def getPatternDetails(int patternId, Token ravToken){
        def patternDetails = getResource('https://api.ravelry.com/patterns/'+ patternId + '.json',ravToken);
        return patternDetails;
    }

    def getStash(String userName, Token ravToken){
        def stash = getResource('https://api.ravelry.com/people/' + userName + '/stash/list.json',ravToken);
        return stash;
    }
}