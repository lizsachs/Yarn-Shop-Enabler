package rav

import uk.co.desirableobjects.oauth.scribe.OauthService;
import org.scribe.model.Token
import groovy.json.JsonSlurper;

class HttpService {

    OauthService oauthService

    def getProjects(String userName, Token ravToken){
        def response = oauthService.getRavelryResource(ravToken, 'https://api.ravelry.com/projects/' + userName + '/list.json');
        def allProjects = null;
        if(response.isSuccessful()){
            allProjects = new JsonSlurper().parseText(response.getBody());
        }

        return allProjects;
    }

    def getProjectDetails(String userName, int projectId, Token ravToken)   {
        def response = oauthService.getRavelryResource(ravToken, 'https://api.ravelry.com/projects/' + userName + '/' + projectId + '.json');
        def projectDetails = null;

        if(response.isSuccessful()){
            projectDetails = new JsonSlurper().parseText(response.getBody());
        }

        return projectDetails;
    }

    def getYarnDetails(int yarnId, Token ravToken){
        def response = oauthService.getRavelryResource(ravToken, 'https://api.ravelry.com/yarns/'+ yarnId + '.json');
        def yarnDetails = null;
        if(response.isSuccessful()){
            yarnDetails = new JsonSlurper().parseText(response.getBody());
        }

        return yarnDetails;
    }

    def getPatternDetails(int patternId, Token ravToken){
        def response = oauthService.getRavelryResource(ravToken, 'https://api.ravelry.com/patterns/'+ patternId + '.json');
        def patternDetails = null;

        if(response.isSuccessful()){
            patternDetails = new JsonSlurper().parseText(response.getBody());
        }

        return patternDetails;
    }

    def getStash(String userName, Token ravToken){
        def response = oauthService.getRavelryResource(ravToken, 'https://api.ravelry.com/people/' + userName + '/stash/list.json');
        def stash = null;

        if(response.isSuccessful()){
            stash = new JsonSlurper().parseText(response.getBody());
        }

        return stash;
    }
}