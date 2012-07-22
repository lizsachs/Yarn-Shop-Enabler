import uk.co.desirableobjects.oauth.scribe.OauthService;
import org.scribe.model.Token
import groovy.json.JsonSlurper;

class httpService {

    OauthService oauthService

    def getProjects(String userName, Token ravToken){
        def response = oauthService.getRavelryResource(ravToken, 'https://api.ravelry.com/projects/' + userName + '/list.json');
        def allProjects = new JsonSlurper().parseText(response.getBody())

        return allProjects;
    }

    def getProjectDetails(String userName, int projectId, Token ravToken)   {
        def response = oauthService.getRavelryResource(ravToken, 'https://api.ravelry.com/projects/' + userName + '/' + projectId + '.json');
        def projectDetails = new JsonSlurper().parseText(response.getBody())

        return projectDetails;
    }

    def getYarnDetails(int yarnId, Token ravToken){
        def response = oauthService.getRavelryResource(ravToken, 'https://api.ravelry.com/yarns/'+ yarnId + '.json');
        def yarnDetails = new JsonSlurper().parseText(response.getBody())

        return yarnDetails;
    }

    def getPatternDetails(int patternId, Token ravToken){
        def response = oauthService.getRavelryResource(ravToken, 'https://api.ravelry.com/patterns/'+ patternId + '.json');
        def patternDetails = new JsonSlurper().parseText(response.getBody())

        return patternDetails;
    }
}