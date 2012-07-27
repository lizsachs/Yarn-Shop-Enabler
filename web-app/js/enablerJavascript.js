
dojo.require("dijit.form.Button");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.CheckBox");
dojo.require("dijit.form.Select");

var chartData;
var stashData;

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
            var userName = dojo.byId('userName').value;
            getProjectData(userName);
            getStashData(userName);
        }
    }, "getDataButton");

});

function getProjectData(userName) {

    dojo.byId("yarnWeightDiv").innerHTML = "";
    dojo.byId("projectTypeDiv").innerHTML = "";

    var xhrArgs = {
        url: "projectData/getProjectStats",
        content:{userName:userName},
        handleAs: "json",
        load: function(data) {
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
        },
        error: function(error) {
            dojo.byId("projectResponse").innerHTML = "Error:" + error;
        }
    }
    dojo.byId("projectResponse").innerHTML = "Calculating project stats..."
    var deferred = dojo.xhrPost(xhrArgs);
}

function getStashData(userName) {

    dojo.byId("stashColumnChartDiv").innerHTML = "";

    var xhrArgs = {
        url: "projectData/getStashStats",
        content:{userName:userName},
        handleAs: "json",
        load: function(data) {
            if(!data['error']){
                stashData = data;
                if(!stashData['message']){
                    genericPieChart(stashData['yarnColorPercent'],stashColorDiv,'Stash By Color',"");
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

    yarnWeightChart(orderedYarnWeightLabels,orderedYarnWeightCounts, 'yarnWeightDiv','Projects by Yarn Weight for ' + projectType + ' Projects', 'Projects', 'Yarn Weight');
    dojo.byId("projectResponse").innerHTML = "";
}

function createDynamicStashCharts() {
    var yarnWeightData = stashData['yarnWeight'];

    var orderedYarnWeightCounts = [];
    for (var weightIndex in orderedYarnWeightLabels) {
        orderedYarnWeightCounts.push(yarnWeightData[orderedYarnWeightLabels[weightIndex]])
    }

    yarnWeightChart(orderedYarnWeightLabels,orderedYarnWeightCounts,'stashColumnChartDiv','Stash by Yarn Weight', 'Stash', 'Yarn Weight');
    dojo.byId("stashResponse").innerHTML = "";
}

function yarnWeightChart(yarnWeightLabels,yarnWeightData, renderTo, title, yAxisLabel, xAxisLabel){
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
                renderTo: 'projectTypeDiv',
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

function genericPieChart(data, renderTo, title, subtitle){
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
                name: title,
                data: data
            }]
        });
    });
}
