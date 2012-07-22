<!doctype html>
<html>
<head>
    <title>SUCCESS!</title>
    <style type="text/css">
    .dojoxGridCell {font-size: 12px;}
    h2 {margin-top: 0;}
    </style>
    <dojo:header theme="Nihilo" showSpinner="true"/>
    <script type="text/javascript">
        dojo.require("dijit.form.Button");
        dojo.require("dijit.form.TextBox");
        dojo.require("dijit.form.CheckBox");

        function sendForm() {
            var form = dojo.byId("myform");

            dojo.connect(form, "onsubmit", function(event) {
                dojo.stopEvent(event);

                var xhrArgs = {
                    form: dojo.byId("myform"),
                    handleAs: "text",
                    load: function(data) {
                        dojo.byId("response").innerHTML = data;
                    },
                    error: function(error) {
                        dojo.byId("response").innerHTML = "Error:" + error;
                    }
                }
                dojo.byId("response").innerHTML = "Form being sent..."
                var deferred = dojo.xhrPost(xhrArgs);
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
<div id="response">
</div>
</body>
</html>
