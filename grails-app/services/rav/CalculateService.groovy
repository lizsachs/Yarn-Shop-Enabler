package rav

class CalculateService {

    def httpService

    // I put all of this in one service call to minimize calls to the database rather than having separate calculations for each type of data, only need to iterate through projects once.
    def countProjectDetails(userName, allProjects, token) {
        def initializeYarnWeightCounts = [
                'Thread':0,
                'Cobweb':0,
                'Lace':0,
                'Light Fingering':0,
                'Fingering':0,
                'Sport':0,
                'DK':0,
                'Worsted':0,
                'Aran / Worsted':0,
                'Aran':0,
                'Bulky':0,
                'Super Bulky':0,
                'Other':0,
                'No Yarn Specified':0];

        def yarnWeightCount = ['All':initializeYarnWeightCounts.clone()];
        def patternTypeCount = ['No Pattern Type Specified':0];
        def totalPatternTypes = 0;

        allProjects.each{
            def project = httpService.getProjectDetails(userName,it.id, token);
            def patternType = 'No Pattern Type Specified';
            totalPatternTypes++;
            if(it.pattern_id){
                def pattern = httpService.getPatternDetails(it.pattern_id,token);
                patternType = pattern.pattern.pattern_type.name;
                if(patternType && patternTypeCount[patternType]){
                    patternTypeCount[patternType]++;
                }
                else if(patternType){
                    patternTypeCount[patternType] = 1;
                }
            }
            else{
                patternTypeCount[patternType]++;
            }

            def projectPacks = project.project.packs;
            def uniqueYarnsPerProject = [];
            // we only want to count each weight of yarn once per project, but if the project uses yarns of two weights we want to count both
            projectPacks.each{
                if(it.yarn_weight && !uniqueYarnsPerProject.contains(it.yarn_weight.name)){
                    uniqueYarnsPerProject.add(it.yarn_weight.name);
                }
            }
            if(!yarnWeightCount[patternType]){
                yarnWeightCount[patternType] = initializeYarnWeightCounts.clone();
            }
            uniqueYarnsPerProject.each{
                    yarnWeightCount[patternType][it]++;
                    yarnWeightCount['All'][it]++;
            }
            if(uniqueYarnsPerProject.size() == 0){
                yarnWeightCount['All']['No Yarn Specified']++;
                yarnWeightCount[patternType]['No Yarn Specified']++;
            }
        }

        def patternTypePercentages = calculatePatternTypePercent(patternTypeCount,totalPatternTypes);

        return ['yarnWeight':yarnWeightCount,'patternTypePercentages':patternTypePercentages,'patternTypes':patternTypeCount.keySet()];
    }

    def calculatePatternTypePercent(patternTypeCount,totalPatternTypes){
        def patternTypePercentages = []
        patternTypeCount.each{
            patternTypePercentages.add([it.key,it.value/totalPatternTypes*100]);
        }
        patternTypePercentages;
    }

    def countStashDetails(userName, stash, token){

    }


}
