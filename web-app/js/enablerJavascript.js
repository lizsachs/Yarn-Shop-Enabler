
dojo.require("dijit.form.Button");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.CheckBox");
dojo.require("dijit.form.Select");
dojo.require("dojox.widget.Standby");
dojo.require("dijit.Dialog");
dojo.require("dojox.grid.DataGrid");
dojo.require("dojo.data.ItemFileWriteStore");
dojo.require("dijit.layout.ContentPane");


// keep data here once it's been processed so we can re-render the charts at will
var chartData;
var stashData;
var userName;

var orderedYarnWeightLabels = [
    'Thread',
    'Cobweb',
    'Lace',
    'Light Fingering',
    'Fingering',
    'Sport',
    'DK',
    'Worsted',
    'Aran / Worsted',
    'Aran',
    'Bulky',
    'Super Bulky',
    'No Yarn Specified'
];

dojo.ready(function(){

    // Create a button programmatically:
    var button = new dijit.form.Button({
        label: "Enable!",
        onClick: function(){
            userName = dojo.byId('userName').value;
            // kick off both calls using AJAX requests so that the user can get charts that run faster more quickly
            getProjectData();
            getStashData();
        }
    }, "getDataButton");

});

function getProjectData() {
    dojo.byId("projectTypeSpan").innerHTML = "";
    dojo.style("projectStandby",{"height":"100px"});
    var projectStandby = new dojox.widget.Standby({
        target: "projectStandby"
    });
    document.body.appendChild(projectStandby.domNode);
    projectStandby.startup();
    projectStandby.show();

    var xhrArgs = {
        url: "projectData/getProjectStats",
        content:{userName:userName},
        handleAs: "json",
        load: dojo.hitch(this,function(data) {
            projectStandby.hide();
            dojo.style("projectStandby",{"height":"0px"}); // even with the .hide(), this is leaving whitespace at the top of the page, so we'll set the height to 0
            if(!data['error']){
                chartData = data;

                if(!chartData['message']){
                    projectTypePieChart(chartData['patternTypePercentages']);

                    createDynamicProjectCharts('All');
                }
                else{
                    dojo.byId('projectResponse').innerHTML = chartData['message'];
                }
            }
            else {
                if(data['error']['errorCode']=='authenticationError'){
                    window.location.href = data['error']['errorURL'];
                }
                else {
                    dojo.byId('projectResponse').innerHTML = 'Sorry, an error occurred.\n' + data['error']['errorCode'];
                }
            }
        }),
        error: function(error) {
            projectStandby.hide();
            dojo.byId("projectResponse").innerHTML = "Error:" + error;
        }
    }
    dojo.byId("projectResponse").innerHTML = "Calculating project stats..."
    var deferred = dojo.xhrPost(xhrArgs);
}

function getStashData() {
    dojo.byId("yarnWeightSpan").innerHTML = "";
    dojo.byId("stashColorSpan").innerHTML = "";
    dojo.style("stashStandby",{"height":"100px"});
    var stashStandby = new dojox.widget.Standby({
        target: "stashStandby"
    });
    document.body.appendChild(stashStandby.domNode);
    stashStandby.startup();
    stashStandby.show();

    dojo.byId("stashColumnChartSpan").innerHTML = "";

    var xhrArgs = {
        url: "projectData/getStashStats",
        content:{userName:userName},
        handleAs: "json",
        load: function(data) {
            stashStandby.hide();
            dojo.style("stashStandby",{"height":"0px"}); // even with the .hide(), this is leaving whitespace at the top of the page, so we'll set the height to 0

            if(!data['error']){
                stashData = data;
                if(!stashData['message']){
                    createDynamicStashCharts();
                }
                else{
                    dojo.byId('stashResponse').innerHTML = chartData['message'];
                }
            }
            else {
                if(data['error']['errorCode']=='authenticationError'){
                    window.location.href = data['error']['errorURL'];
                }
                else {
                    dojo.byId('stashResponse').innerHTML = 'Sorry, an error occurred.\n' + data['error']['errorCode'];
                }
            }
        },
        error: function(error) {
            stashStandby.hide();
            dojo.byId("stashResponse").innerHTML = "Error:" + error;
        }
    }
    dojo.byId("stashResponse").innerHTML = "Calculating stash stats..."
    var deferred = dojo.xhrPost(xhrArgs);
}

function createDynamicProjectCharts(projectType) {
    var yarnWeightData = chartData['yarnWeight'][projectType];

    var orderedYarnWeightCounts = [];
    for (var weightIndex in orderedYarnWeightLabels) {
        orderedYarnWeightCounts.push(yarnWeightData[orderedYarnWeightLabels[weightIndex]])
    }

    yarnWeightChart(orderedYarnWeightLabels,orderedYarnWeightCounts, 'yarnWeightSpan','Projects by Yarn Weight for ' + projectType + ' Projects', 'Projects', 'Yarn Weight', projectType, "project");
    dojo.byId("projectResponse").innerHTML = "";
}

function createDynamicStashCharts() {
    var yarnWeightData = stashData['yarnWeight'];
    var colorData = stashData['yarnColors'];

    var orderedYarnWeightCounts = [];
    for (var weightIndex in orderedYarnWeightLabels) {
        orderedYarnWeightCounts.push(yarnWeightData[orderedYarnWeightLabels[weightIndex]])
    }

    var orderedColorPercentages = [];
    var orderedColorsHex = [];
    for (var colorIndex in colorData){
        if(colorData[colorIndex]['percentage'] != 0){
            orderedColorPercentages.push([colorIndex,colorData[colorIndex]['percentage']])
            orderedColorsHex.push(colorData[colorIndex]['color'])
        }
    }
    genericPieChart(orderedColorPercentages,stashColorSpan,'Stash By Color',"click a pie slick to see stash included in dataset",orderedColorsHex);

    yarnWeightChart(orderedYarnWeightLabels,orderedYarnWeightCounts,'stashColumnChartSpan','Stash by Yarn Weight', 'Stash', 'Yarn Weight', null, "stash");
    dojo.byId("stashResponse").innerHTML = "";
}

function formatImage(src) {
    if(!src){
        return "no photo";
    }
    return '<img src="' + src + '" style="width: 75px; height: 75px;">'
}

function showDetailsDialog(dataType,projectType,qualifier){

    if(dijit.byId("dataDialog")){
        dijit.byId("dataDialog").destroyRecursive();
    }

    var dataContentPane = new dijit.layout.ContentPane({
        content:"Click a row to view the corresponding page in Ravelry.<br><br><div id='dataGridDiv'></div>"
    });

    var dialog = new dijit.Dialog({
        title: (projectType ? projectType : "") + " " + (dataType == "project" ? "Project" : "Stash") + " In " + qualifier + " Yarn",
        id: "dataDialog",
        content:dataContentPane
    });

    dialog.show();

    var data = {
        identifier: 'id',
        items: []
    };

    var data_list = null;
    if(dataType == "project"){
        data_list = chartData['patternMetadata'][projectType][qualifier];
    }
    else if(dataType == "stash"){
        data_list = stashData['yarnWeightMetadata'][qualifier];
    }
    else if(dataType == "stashColor"){
        data_list = stashData['yarnColorMetadata'][qualifier];
    }

    for(var i=0; i<data_list.length; i++){
        data.items.push(dojo.mixin({ id: i }, data_list[i]));
    }

    var store = new dojo.data.ItemFileWriteStore({data: data});

    /*set up layout*/
    var layout = [[
        {'name': 'Photo', 'field': 'photoUrl', 'width':'80px', formatter: formatImage},
        {'name': 'Name', 'field': 'name', 'width': '200px'}
    ]];

    /*create a new grid:*/
    var foo = document.createElement("div");
    var grid = new dojox.grid.DataGrid({
            id: 'grid',
            store: store,
            structure: layout,
            autoWidth:true,
            rowHeight:80,
            autoHeight:7,
            onRowClick: function(e){
                var urlString = null;
                if(dataType.indexOf('stash') !== -1){
                    urlString = 'http://www.ravelry.com/people/' + userName + "/stash/" + grid._getItemAttr(e.rowIndex,'permalink')
                }
                else if(dataType == "project"){
                    urlString = 'http://www.ravelry.com/projects/' + userName + "/" + grid._getItemAttr(e.rowIndex,'permalink')
                }
                window.open(urlString)
            }
        },
        'grid');

    /*append the new grid to the div*/
    dojo.byId("dataGridDiv").appendChild(grid.domNode);

    /*Call startup() to render the grid*/
    dojo.style('dataDialog','top','200px');
    grid.startup();
}


function yarnWeightChart(yarnWeightLabels,yarnWeightData, renderTo, title, yAxisLabel, xAxisLabel, projectType, dataType){
    var chart;
    $(document).ready(function() {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: renderTo,
                type: 'column',
                margin: [ 50, 50, 100, 80]
            },
            title: {
                text: title
            },
            subtitle: {
                text: 'click a column to see items included in dataset'
            },
            xAxis: {
                categories: yarnWeightLabels,
                labels: {
                    rotation: -45,
                    align: 'right',
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif'
                    }
                }
            },
            yAxis: {
                min: 0,
                title: {
                    text: yAxisLabel
                }
            },
            legend: {
                enabled: false
            },
            tooltip: {
                formatter: function() {
                    return '<b>'+ this.x +'</b><br/>'+
                        this.y + ((this.y==1) ? ' project' : ' projects');
                }
            },
            series: [{
                name: xAxisLabel,
                data: yarnWeightData,
                dataLabels: {
                    enabled: true,
                    rotation: -90,
                    color: '#FFFFFF',
                    align: 'right',
                    x: -3,
                    y: 10,
                    formatter: function() {
                        return this.y;
                    },
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif'
                    }
                },
                point: {
                    events: {
                        click: function(event) {
                            showDetailsDialog(dataType,projectType,this.category);
                        }
                    }
                }
            }]
        });
    });
}

function projectTypePieChart(patternTypeData){
    var chart;
    $(document).ready(function() {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'projectTypeSpan',
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: 'Project Types'
            },
            subtitle: {
                text: 'click a pie slice to see yarn weight data for selected project type'
            },
            tooltip: {
                formatter: function() {
                    return '<b>'+ this.point.name +'</b>: '+ Math.round(this.percentage*10)/10 +' %';
                }
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    events: {
                        click: function(event){
                            if(event.point.selected){createDynamicProjectCharts('All'); }
                            else{createDynamicProjectCharts(event.point.name);}
                        }
                    },
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        color: '#000000',
                        connectorColor: '#000000',
                        formatter: function() {
                            return '<b>'+ this.point.name +'</b>: '+ Math.round(this.percentage*10)/10 +' %';
                        }
                    }
                }
            },
            series: [{
                type: 'pie',
                name: 'Project Pattern Types',
                data: patternTypeData
            }]
        });
    });
}

function genericPieChart(data, renderTo, title, subtitle, hexColors){
    var chart;
    $(document).ready(function() {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: renderTo,
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
            },
            title: {
                text: title
            },
            subtitle: {
                text: subtitle
            },
            tooltip: {
                formatter: function() {
                    return '<b>'+ this.point.name +'</b>: '+ Math.round(this.percentage*10)/10 +' %';
                }
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    events: {
                        click: function(event){
                            showDetailsDialog('stashColor',null,event.point.name);
                        }
                    },
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        color: '#000000',
                        connectorColor: '#000000',
                        formatter: function() {
                            return '<b>'+ this.point.name +'</b>: '+ Math.round(this.percentage*10)/10 +' %';
                        }
                    }
                }
            },
            colors: hexColors,
            series: [{
                type: 'pie',
                name: title,
                data: data
            }]
        });
    });
}
