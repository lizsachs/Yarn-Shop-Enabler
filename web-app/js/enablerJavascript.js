
dojo.require("dijit.form.Button");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.CheckBox");
dojo.require("dijit.form.Select");

var chartData;

function sendForm() {
    var form = dojo.byId("userNameForm");

    function createPatternTypeSelect() {
        var patternTypeDropdownOptions = [
            {'label':'All', 'value':'All', 'selected':true}
        ];
        var patternTypes = chartData['projectStats']['patternTypes'].sort();
        for (var patternTypeIndex in patternTypes) {
            patternTypeDropdownOptions.push({'label':patternTypes[patternTypeIndex], 'value':patternTypes[patternTypeIndex]});
        }

        dojo.ready(function () {
            var patternTypeSelect = new dijit.form.Select({
                name:'patternTypeSelect',
                options:patternTypeDropdownOptions,
                onChange:function (b) {
                    createDynamicCharts(patternTypeSelect.get('value'))
                }
            }).placeAt(dojo.byId('projectTypeSelect'));
        });
    }

    dojo.connect(form, "onsubmit", function(event) {
        dojo.stopEvent(event);
        dojo.byId("yarnWeightDiv").innerHTML = "";
        dojo.byId("projectTypeDiv").innerHTML = "";
        dojo.byId("projectTypeSelect").innerHTML = "";

        var xhrArgs = {
            form: dojo.byId("userNameForm"),
            handleAs: "json",
            load: function(data) {
                chartData = data;
                projectTypePieChart(chartData['projectStats']['patternTypePercentages']);

                createDynamicCharts('All');

                createPatternTypeSelect();
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

function createDynamicCharts(projectType) {
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

    yarnWeightChart(orderedYarnWeightLabels,orderedYarnWeightCounts);
    dojo.byId("response").innerHTML = "";
}

function yarnWeightChart(yarnWeightLabels,yarnWeightData){
    var chart;
    $(document).ready(function() {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'yarnWeightDiv',
                type: 'column',
                margin: [ 50, 50, 100, 80]
            },
            title: {
                text: 'Projects by Yarn Weight'
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
                name: 'Browser share',
                data: patternTypeData
            }]
        });
    });

}
