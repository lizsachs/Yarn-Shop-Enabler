<!doctype html>
<html>
<head>
    <title>Yarn Store Enabler</title>
    <style type="text/css">
        /*Grid need a explicit width/height by default*/
    #grid {
        width: 43em;
        height: 75px;
    }
    .dojoxGridCell {font-size: 12px;}

    </style>
    <dojo:header theme="claro" />
</head>
<body class="claro">
<div id="page-body" role="main">
    <div align="center"><h1>Local Yarn Store Enabler</h1>
    <blockquote>
        <div class='info_message' style="color:red">
            ${flash.message}
        </div>
        <p>Please connect with Ravelry to begin!</p>
        <oauth:connect provider="ravelry"><button data-dojo-type="dijit.form.Button" type="button">Connect to Ravelry

        <script type="dojo/method" data-dojo-event="onClick" data-dojo-args="evt">
            </script>
        </button></oauth:connect>
    </blockquote>
    </div>
</body>
</html>
