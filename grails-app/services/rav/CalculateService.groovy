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

    def initializeColorMap = [
            'Black':null,
            'Blue':null,
            'Blue-green':null,
            'Blue-purple':null,
            'Brown':null,
            'Gray':null,
            'Green':null,
            'Natural/Undyed':null,
            'Orange':null,
            'Pink':null,
            'Purple':null,
            'Red':null,
            'Red-orange':null,
            'Red-purple':null,
            'White':null,
            'Yellow':null,
            'Yellow-green':null,
            'Yellow-orange':null,
            'No Color Specified':null
    ]

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

                def photoUrl = null;
                project.project.photos.each {
                    if (it.sort_order == 1){
                        photoUrl = it.square_url;
                    }
                }
                def projectMetadata = newObjectMetadata(project.project.name,project.project.permalink,photoUrl)

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

    def newObjectMetadata(name,permalink,photoUrl){
        def objectMetadata = ['name': null, 'permalink': null, 'photoUrl': null];
        objectMetadata['name'] = name;
        objectMetadata['permalink'] = permalink;
        objectMetadata['photoUrl'] = photoUrl;
        return objectMetadata;
    }

    def initializeMapToZero (map){
        def initializedMap = map.collectEntries{key,value -> [key,0]};
        return initializedMap;
    }

    def initializeMapToMetadataContainer(map){
        def initializedMap = map.collectEntries{key,value -> [key,[]]}
        return initializedMap;
    }

    def initializeYarnColorCounts(map){

        def initializedMap = map.collectEntries{key,value -> [key,['count':0,'color':null]]}

        initializedMap['Black']['color'] = '#000000';
        initializedMap['Blue']['color'] ='#92A8CD';
        initializedMap['Blue-green']['color'] = '#3D96AE';
        initializedMap['Blue-purple']['color'] = '#666699';
        initializedMap['Brown']['color'] = '#A47D7C';
        initializedMap['Gray']['color'] = '#CCCCCC';
        initializedMap['Green']['color'] = '#89A54E';
        initializedMap['Natural/Undyed']['color'] = '#FFFFE0';
        initializedMap['Orange']['color'] = '#DB843D';
        initializedMap['Pink']['color'] = '#FF9EDF';
        initializedMap['Purple']['color'] = '#80699B';
        initializedMap['Red']['color'] = '#AA4643';
        initializedMap['Red-orange']['color'] = '#FF5C33';
        initializedMap['Red-purple']['color'] = '#A3297A';
        initializedMap['White']['color'] = '#FFFFFF';
        initializedMap['Yellow']['color'] = '#FFD119';
        initializedMap['Yellow-green']['color'] = '#B5CA92';
        initializedMap['Yellow-orange']['color'] = '#FFCC00';
        initializedMap['No Color Specified']['color'] = '#E6E6E6';

        return initializedMap;
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
        def yarnColorCount =  initializeYarnColorCounts(initializeColorMap.clone());

        def yarnColorMetadata = initializeMapToMetadataContainer(initializeColorMap.clone());
        def yarnWeightMetadata = initializeMapToMetadataContainer(initializeYarnWeightCounts.clone());

        def totalStash = 0;

        stash.stash.each{
            if(it['stash_status']['id'] == 1){
                def yarn = it.yarn;
                totalStash++;

                def photoUrl = it.has_photo ? it.first_photo.square_url : null;
                def yarnMetadata = newObjectMetadata(it.name,it.permalink,photoUrl);

                if(yarn && yarn.yarn_weight){
                    yarnWeightCount[yarn.yarn_weight.name]++;
                    yarnWeightMetadata[yarn.yarn_weight.name].add(yarnMetadata);
                }

                if(it.color_family_name && yarnColorCount[it.color_family_name]){
                    yarnColorCount[it.color_family_name]['count']++;
                    yarnColorMetadata[it.color_family_name].add(yarnMetadata);
                }
                else {
                    yarnColorCount['No Color Specified']['count']++;
                    yarnColorMetadata['No Color Specified'].add(yarnMetadata);
                }
            }
        }

        //not using the generic calculate percentages method because I want to store the results along with color values
        yarnColorCount.each{
            it.value['percentage'] = (it.value['count']/totalStash)*100;
        }
        return ['yarnWeight':yarnWeightCount, 'yarnColors':yarnColorCount, 'yarnColorMetadata':yarnColorMetadata, 'yarnWeightMetadata': yarnWeightMetadata]
    }


}
