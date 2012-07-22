package rav

class CalculateService {

    def httpService

    def countProjectDetails(userName, allProjects, token) {
        def yarnWeightCount = [:]
        def patternTypeCount = [:]
        allProjects.each{
            def project = httpService.getProjectDetails(userName,it.id, token);

            if(it.pattern_id){
                def pattern = httpService.getPatternDetails(it.pattern_id,token);
                def patternType = pattern.pattern.pattern_type.name;
                if(patternType && patternTypeCount[patternType]){
                    patternTypeCount[patternType]++;
                }
                else{
                    patternTypeCount[patternType] = 1;
                }

            }

            def projectPacks = project.project.packs;
            def uniqueYarnsPerProject = [];
            def uniqueFibersPerProject = [];
            // we only want to count each weight of yarn once per project, but if the project uses yarns of two weights we want to count both
            projectPacks.each{
                if(it.yarn_weight && !uniqueYarnsPerProject.contains(it.yarn_weight.name)){
                    uniqueYarnsPerProject.add(it.yarn_weight.name);
                }
            }
            uniqueYarnsPerProject.each{
                if(yarnWeightCount[it]) {
                    yarnWeightCount[it] = yarnWeightCount[it]++;
                }
                else {
                    yarnWeightCount[it] = 1;
                }
            }
        }
        return ['yarnWeight':yarnWeightCount,'patternType':patternTypeCount];
    }
}
