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
            'Thread':null,
            'Cobweb':null,
            'Lace':null,
            'Light Fingering':null,
            'Fingering':null,
            'Sport':null,
            'DK':null,
            'Worsted':null,
            'Aran / Worsted':null,
            'Aran':null,
            'Bulky':null,
            'Super Bulky':null,
            'Other':null,
            'No Yarn Specified':null];

    // I put all of this in one service call to minimize calls to the database rather than having separate calculations for each type of data, only need to iterate through projects once.
    def countProjectDetails(userName, allProjects, token) {

        def yarnWeightCount = ['All':initializeMapToZero(initializeYarnWeightCounts.clone())];
        def patternMetadata = ['All':initializeMapToMetadataContainer(initializeYarnWeightCounts.clone())]
        def patternTypeCount = ['No Pattern Type Specified':0];
        def totalPatternTypes = 0;

        allProjects.each{
            def project = httpService.getProjectDetails(userName,it.id, token);
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

                yarnWeightCount[patternType] = yarnWeightCount.get(patternType,initializeMapToZero(initializeYarnWeightCounts.clone()));
                patternMetadata[patternType] = patternMetadata.get(patternType,initializeMapToMetadataContainer(initializeYarnWeightCounts.clone()))

                def projectMetadata = ['name': project.project.name, 'permalink': project.project.permalink, 'photoUrl': null];

                project.project.photos.each {
                    if (it.sort_order == 1){
                        projectMetadata['photoUrl'] = it.square_url;
                    }
                }

                // if more than one yarn weight is used per project, we'll just increment the data for each yarn weight since the project basically belongs to both categories
                projectPacks.each{
                    if(it.yarn_weight && !uniqueYarnsPerProject.contains(it.yarn_weight.name)){
                        uniqueYarnsPerProject.add(it.yarn_weight.name);
                    }
                }

                uniqueYarnsPerProject.each{
                    yarnWeightCount[patternType][it]++;
                    yarnWeightCount['All'][it]++;

                    patternMetadata[patternType][it].add(projectMetadata);
                    patternMetadata['All'][it].add(projectMetadata);
                }
                if(uniqueYarnsPerProject.size() == 0){
                    yarnWeightCount['All'][noYarnSpecified]++;
                    yarnWeightCount[patternType][noYarnSpecified]++;


                    patternMetadata[patternType][noYarnSpecified].add(projectMetadata);
                    patternMetadata['All'][noYarnSpecified].add(projectMetadata);
                }
            }
        }

        def patternTypePercentages = calculatePercentages(patternTypeCount,totalPatternTypes);

        return ['yarnWeight':yarnWeightCount,'patternTypePercentages':patternTypePercentages,'patternTypes':patternTypeCount.keySet(),'patternMetadata':patternMetadata];
    }

    def initializeMapToZero (map){
        def initializedMap = map.collectEntries{key,value -> [key,0]};
        return initializedMap;
    }

    def initializeMapToMetadataContainer(map){
        def initializedMap = map.collectEntries{key,value -> [key,[]]}
    }

    def calculatePercentages(counts,total){
        def percentages = []
        counts.each{
            percentages.add([it.key,it.value/total*100]);
        }
        percentages;
    }

    def countStashDetails(userName, stash, token){
        def yarnWeightCount = initializeMapToZero(initializeYarnWeightCounts.clone());
        // yarnColorCount isn't global because clone copies the references for the values in the more complex key-value pairs,
        // which messes up the counts.  Sticking it here because I haven't bothered to write a deep-clone method yet
        // storing hex values for each color here so that I can keep them paired with the right category for display purposes
        def yarnColorCount =  [
                'Black':['count':0,'color':'#000000'],
                'Blue':['count':0,'color':'#92A8CD'],
                'Blue-green':['count':0,'color':'#3D96AE'],
                'Blue-purple':['count':0,'color':'#666699'],
                'Brown':['count':0,'color':'#A47D7C'],
                'Gray':['count':0,'color':'#CCCCCC'],
                'Green':['count':0,'color':'#89A54E'],
                'Natural/Undyed':['count':0,'color':'#FFFFE0'],
                'Orange':['count':0,'color':'#DB843D'],
                'Pink':['count':0,'color':'#FF9EDF'],
                'Purple':['count':0,'color':'#80699B'],
                'Red':['count':0,'color':'#AA4643'],
                'Red-orange':['count':0,'color':'#FF5C33'],
                'Red-purple':['count':0,'color':'#A3297A'],
                'White':['count':0,'color':'#FFFFFF'],
                'Yellow':['count':0,'color':'#FFD119'],
                'Yellow-green':['count':0,'color':'#B5CA92'],
                'Yellow-orange':['count':0,'color':'#FFCC00'],
                'No Color Specified':['count':0,'color':'#E6E6E6']
        ];

        def totalStash = 0;

        stash.stash.each{
            if(it['stash_status']['id'] == 1){
                def yarn = it.yarn;
                totalStash++;

                if(yarn && yarn.yarn_weight){
                    yarnWeightCount[yarn.yarn_weight.name]++;
                }

                if(it.color_family_name && yarnColorCount[it.color_family_name]){
                    yarnColorCount[it.color_family_name]['count']++;
                }
                else {
                    if(it.color_family_name){
                        println(it.color_family_name)
                    }
                    yarnColorCount['No Color Specified']['count']++;
                }
            }
        }

        //not using the generic calculate percentages method because I want to store the results along with color values
        yarnColorCount.each{
            it.value['percentage'] = (it.value['count']/totalStash)*100;
        }
        return ['yarnWeight':yarnWeightCount, 'yarnColors':yarnColorCount]
    }


}
