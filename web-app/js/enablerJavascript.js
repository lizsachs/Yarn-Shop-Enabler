
dojo.require("dijit.form.Button");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.CheckBox");
dojo.require("dijit.form.Select");

var chartData;

function sendForm() {
    var form = dojo.byId("userNameForm");

    dojo.connect(form, "onsubmit", function(event) {
        dojo.stopEvent(event);
        dojo.byId("yarnWeightDiv").innerHTML = "";
        dojo.byId("projectTypeDiv").innerHTML = "";

        var xhrArgs = {
            form: dojo.byId("userNameForm"),
            handleAs: "json",
            load: function(data) {
                if(!data['error']){
                    chartData = data;
                    if(!chartData['projectStats']['message']){
                        projectTypePieChart(chartData['projectStats']['patternTypePercentages']);

                        createDynamicProjectCharts('All');
                    }
                    else{
                        dojo.byId('response').innerHTML = chartData['projectStats']['message'];
                    }
                }
                else {
                    if(data['error']['errorCode']=='authenticationError'){
                        window.location.href = data['error']['errorURL'];
                    }
                    else {
                        dojo.byId('response').innerHTML = 'Sorry, an error occurred.\n' + data['error']['errorCode'];
                    }
                }
            },
            error: function(error) {
                dojo.byId("response").innerHTML = "Error:" + error;
            }
        }
        dojo.byId("response").innerHTML = "Calculating project stats..."
        var deferred = dojo.xhrPost(xhrArgs);
    });
}

dojo.addOnLoad(sendForm);

function createDynamicProjectCharts(projectType) {
    var yarnWeightData = chartData['projectStats']['yarnWeight'][projectType];
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

    var orderedYarnWeightCounts = [];
    for (var weightIndex in orderedYarnWeightLabels) {
        orderedYarnWeightCounts.push(yarnWeightData[orderedYarnWeightLabels[weightIndex]])
    }

    yarnWeightChart(orderedYarnWeightLabels,orderedYarnWeightCounts, projectType);
    dojo.byId("response").innerHTML = "";
}

function yarnWeightChart(yarnWeightLabels,yarnWeightData, projectType){
    var chart;
    $(document).ready(function() {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'yarnWeightDiv',
                type: 'column',
                margin: [ 50, 50, 100, 80]
            },
            title: {
                text: 'Projects by Yarn Weight for ' + projectType + ' Projects'
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
                    text: 'Projects'
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
                name: 'Yarn Weight',
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
                name: 'Browser share',
                data: patternTypeData
            }]
        });
    });

}
