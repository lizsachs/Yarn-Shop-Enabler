package rav

class CalculateService {

    def httpService

    def noYarnSpecified = 'No Yarn Specified';

    def initializeYardageMap = [
            ['0-150':['count':0,'min':0,'max':150]],
            ['150-300':['count':0,'min':150,'max':300]],
            ['300-450':['count':0,'min':300,'max':450]],
            ['450-600':['count':0,'min':450,'max':600]],
            ['600-750':['count':0,'min':600,'max':750]],
            ['750-900':['count':0,'min':750,'max':900]],
            ['900-1200':['count':0,'min':900,'max':1200]],
            ['1200-1500':['count':0,'min':1200,'max':1500]],
            ['1500-1800':['count':0,'min':1500,'max':1800]],
            ['1800-2100':['count':0,'min':1800,'max':2100]],
            ['2100+':['count':0,'min':2100,'max':Integer.MAX_VALUE]]
    ];

    def initializeYarnWeightCounts = [
            'Thread':[count:0, yardage:initializeYardageMap.clone()],
            'Cobweb':[count:0, yardage:initializeYardageMap.clone()],
            'Lace':[count:0, yardage:initializeYardageMap.clone()],
            'Light Fingering':[count:0, yardage:initializeYardageMap.clone()],
            'Fingering':[count:0, yardage:initializeYardageMap.clone()],
            'Sport':[count:0, yardage:initializeYardageMap.clone()],
            'DK':[count:0, yardage:initializeYardageMap.clone()],
            'Worsted':[count:0, yardage:initializeYardageMap.clone()],
            'Aran / Worsted':[count:0, yardage:initializeYardageMap.clone()],
            'Aran':[count:0, yardage:initializeYardageMap.clone()],
            'Bulky':[count:0, yardage:initializeYardageMap.clone()],
            'Super Bulky':[count:0, yardage:initializeYardageMap.clone()],
            'Other':[count:0, yardage:initializeYardageMap.clone()],
            'No Yarn Specified':[count:0, yardage:initializeYardageMap.clone()]];

    // I put all of this in one service call to minimize calls to the database rather than having separate calculations for each type of data, only need to iterate through projects once.
    def countProjectDetails(userName, allProjects, token) {

        def yarnWeightCount = ['All':initializeYarnWeightCounts.clone()];
        def patternTypeCount = ['No Pattern Type Specified':0];
        def totalPatternTypes = 0;

        allProjects.each{
            def project = httpService.getProjectDetails(userName,it.id, token);
            def yarnYardage = project.project.packs.total_yards;
            def patternType = 'No Pattern Type Specified';
            totalPatternTypes++;
            if(it.pattern_id){
                def pattern = httpService.getPatternDetails(it.pattern_id,token);
                patternType = pattern ? pattern.pattern.pattern_type.name : patternType;
                if(patternType) {
                    patternTypeCount[patternType] = patternTypeCount.get(patternType, 0) + 1;
                }
            }
            else{
                patternTypeCount[patternType]++;
            }
            if(project != null){
                def projectPacks = project.project.packs;
                def uniqueYarnsPerProject = [];
                // we only want to count each weight of yarn once per project, but if the project uses yarns of two weights we want to count both
                projectPacks.each{
                    if(it.yarn_weight && !uniqueYarnsPerProject.contains(it.yarn_weight.name)){
                        uniqueYarnsPerProject.add(it.yarn_weight.name);
                    }
                }

                yarnWeightCount[patternType] = yarnWeightCount.get(patternType,initializeYarnWeightCounts.clone());

                uniqueYarnsPerProject.each{
                    yarnWeightCount[patternType][it]++;
                    yarnWeightCount['All'][it]++;
                }
                if(uniqueYarnsPerProject.size() == 0){
                    yarnWeightCount['All'][noYarnSpecified]++;
                    yarnWeightCount[patternType][noYarnSpecified]++;
                }
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
