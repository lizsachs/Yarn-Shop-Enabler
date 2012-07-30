package rav

class CalculateService {

    def httpService

    def noYarnSpecified = 'No Yarn Specified';

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

    // calculate the project data.  Count projects at each yarn weight and each type of project.  Store in a project type -> yarn weight hierarchy so that the charts can be interactive
    // and reload the weight counts based on project type
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

    // I use the same code in the front end for displaying object metadata for all of the graphs, so I initialize it generically and want to make sure they always stay in sync.
    def newObjectMetadata(name,permalink,photoUrl){
        def objectMetadata = ['name': null, 'permalink': null, 'photoUrl': null];
        objectMetadata['name'] = name;
        objectMetadata['permalink'] = permalink;
        objectMetadata['photoUrl'] = photoUrl;
        return objectMetadata;
    }

    // a set of initializers to help avoide deep-copy issues with multi-tiered maps.  Construct the top level keys in one place, then initialize the deeper levels as needed.
    def initializeMapToZero (map){
        def initializedMap = map.collectEntries{key,value -> [key,0]};
        return initializedMap;
    }

    def initializeMapToMetadataContainer(map){
        def initializedMap = map.collectEntries{key,value -> [key,[]]}
        return initializedMap;
    }

    // I'm storing the hex colors here so that I can make sure the pie slice colors match the categories in the front end
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

    // return an array of percentages that matches the counts passed in
    def calculatePercentages(counts,total){
        def percentages = []
        counts.each{
            percentages.add([it.key,it.value/total*100]);
        }
        percentages;
    }

    // calculate metrics for stash. We're interested in how many stash items are in each of the yarn weight and yarn color categories
    // simultaneously build up lists of stash metadata that falls into each category so that we can display metadata summaries
    def countStashDetails(userName, stash, token){
        def yarnWeightCount = initializeMapToZero(initializeYarnWeightCounts.clone());
        def yarnColorCount =  initializeYarnColorCounts(initializeColorMap.clone());

        def yarnColorMetadata = initializeMapToMetadataContainer(initializeColorMap.clone());
        def yarnWeightMetadata = initializeMapToMetadataContainer(initializeYarnWeightCounts.clone());

        def totalStash = 0;

        stash.stash.each{
            //only counting stash that is still in hand; if stash status is null, handle that case by ignoring it
            if(it['stash_status'] && it['stash_status']['id'] == 1){
                def yarn = it.yarn;
                totalStash++;

                def photoUrl = it.has_photo ? it.first_photo.square_url : null;
                def yarnMetadata = newObjectMetadata(it.name,it.permalink,photoUrl);

                if(yarn && yarn.yarn_weight){
                    println(it.name);
                    //todo: I should probably handle this by adding weights that aren't in the main list to my map, but I don't have time right this moment, so we'll skip weird sized yarn
                    if(yarnWeightCount.containsKey(yarn.yarn_weight.name)){
                        yarnWeightCount[yarn.yarn_weight.name]++;
                        yarnWeightMetadata[yarn.yarn_weight.name].add(yarnMetadata);
                    }
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
