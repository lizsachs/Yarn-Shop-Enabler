<!doctype html>
<html>
<head>
    <title>Enable!</title>
    <style type="text/css">
    .dojoxGridCell {font-size: 12px;}
    h2 {margin-top: 0;}
    </style>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
    <script src="js/Highcharts-2/js/highcharts.src.js" type="text/javascript"></script>

    <dojo:header theme="Nihilo" />
    <script src="js/enablerJavascript.js" type="text/javascript" >
    </script>
</head>
<body class="Nihilo">
<h1>Yarn Store Enabler</h1>
<blockquote>
    Which Raveler would you like to enable? (This may take a few moments for active fiber enthusiasts!)
    <input type="text" dojoType="dijit.form.TextBox" name="userName" id="userName" value="blacktabi">
</input>
    <div id="getDataButton"></div>
</blockquote>


<div align="center">
    Project Data
    <span id="projectResponse"></span>
    <div id="projectStandby" style="width: 800px; height:100px; background-color: white; "></div>

    <span id="projectTypeSpan" style="width:45%; float:left"></span>
    <span id="yarnWeightSpan" style="width:45%;"></span>
</div>

<hr>

<div align="center">
    Stash Data
<span id="stashResponse"></span>
<div id="stashStandby" style="width: 800px; height:100px; background-color: white; "></div>

<span id="stashColumnChartDiv" style="width:100%"></span>
    <div id="stashColorDiv" style="width:45%; float:left"></div>
    <div id="stashFiberDiv" style="width:45%"></div>
</div>

</body>
</html>
