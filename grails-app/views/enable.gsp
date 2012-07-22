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
    <form action="ProjectData/getUserData" id="userNameForm">
        <input type="text" dojoType="dijit.form.TextBox" name="userName" value="blacktabi">
    </input>
        <button type="submit" dojoType="dijit.form.Button" id="submitButton">
            Enable!
        </button>
    </form>
</blockquote>
<br>
<div id="response"></div>
<table>
    <tr>
<td><div id="yarnWeightDiv" style="width: 50%; height: 300px"></div></td>
<td><div id="projectTypeDiv" style="width: 50%; height: 300px"></div></td>
    </tr>
</table>

</body>
</html>
