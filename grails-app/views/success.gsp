<!doctype html>
<html>
<head>
    <title>SUCCESS!</title>
    <style type="text/css">
    .dojoxGridCell {font-size: 12px;}
    h2 {margin-top: 0;}
    </style>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
    <script src="js/Highcharts-2/js/highcharts.src.js" type="text/javascript"></script>
    <script type="text/javascript" src="js/Highcharts-2/js/themes/default.js"></script>

    <dojo:header theme="Nihilo" />
    <script type="text/javascript">
        dojo.require("dijit.form.Button");
        dojo.require("dijit.form.TextBox");
        dojo.require("dijit.form.CheckBox");

        var chartData;

        function sendForm() {
            var form = dojo.byId("myform");

            dojo.connect(form, "onsubmit", function(event) {
                dojo.stopEvent(event);

                var xhrArgs = {
                    form: dojo.byId("myform"),
                    handleAs: "json",
                    load: function(data) {
                        chartData = data;
                        createCharts('All');
                    },
                    error: function(error) {
                        dojo.byId("response").innerHTML = "Error:" + error;
                    }
                }
                dojo.byId("response").innerHTML = "Calculating project stats..."
                var deferred = dojo.xhrPost(xhrArgs);
            });
        }

        function createCharts(projectType) {
            var yarnWeightData = chartData['yarnWeight'][projectType];
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
            projectTypePieChart(chartData['patternTypePercentages']);
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
                            return '<b>'+ this.point.name +'</b>: '+ this.percentage +' %';
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
                                    return '<b>'+ this.point.name +'</b>: '+ this.percentage +' %';
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
        dojo.addOnLoad(sendForm);
    </script>
</head>
<body class="Nihilo">
<br>
<blockquote>
        Which user would you like to enable?
    <form action="ProjectData/getUserData" id="myform">
        <input type="text" dojoType="dijit.form.TextBox" name="userName" value="blacktabi">
    </input>
        <button type="submit" dojoType="dijit.form.Button" id="submitButton">
            Enable!
        </button>
    </form>
</blockquote>
<br>
<div id="response"></div>
<div id="yarnWeightDiv" style="width: 50%; height: 300px"></div>
<br>
<div id="projectTypeDiv" style="width: 50%; height: 300px"></div>

</body>
</html>
