<!doctype html>
<html>
<head>

    <title>Enable!</title>
    <style type="text/css">
        /*Grid need a explicit width/height by default*/
    #grid {
        width: 43em;
        height: 20em;
    }

    .dojoxGridCell {font-size: 12px;}

    </style>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
    <script src="js/Highcharts-2/js/highcharts.src.js" type="text/javascript"></script>
    <link href="plugins/dojo-1.6.1.7/js/dojo/1.6.1/dojox/grid/resources/claroGrid.css" type="text/css" rel="stylesheet">
    <dojo:header theme="claro" />
    <script src="js/enablerJavascript.js" type="text/javascript" >
    </script>
</head>
<body class="claro">
<div align="center"><h1>Yarn Store Enabler</h1>
    <blockquote>
        <p>Which Raveler would you like to enable? (This may take a few moments for active fiber enthusiasts!)</p>
        <input type="text" dojoType="dijit.form.TextBox" name="userName" id="userName" value="blacktabi">
    </input>
        <div id="getDataButton"></div>
    </blockquote>
</div>

<div align="center">
    <h2>Project Data</h2>
    <span id="projectResponse"></span>
    <div id="projectStandby" style="width: 800px; height:100px; background-color: white; "></div>

    <span id="projectTypeSpan" style="width:45%; float:left"></span>
    <span id="yarnWeightSpan" style="width:45%;"></span>
</div>

<hr>

<div align="center">
    <h2>Stash Data</h2>
    <span id="stashResponse"></span>
    <div id="stashStandby" style="width: 800px; height:100px; background-color: white; "></div>

    <span id="stashColumnChartSpan" style="width:45%; float:left"></span>
    <span id="stashColorSpan" style="width:45%; "></span>
</div>

</body>
</html>
