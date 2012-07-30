Yarn Shop Enabler

This app looks at the data in a specified Raveler's notebook and displays statistics about the Raveler's yarn usage. A fiber enthusiast could use this data to help determine which or how much yarn to buy while shopping if they don't have a specific goal in mind (not that any knitters I know would buy yarn with no purpose intended...).  Questions like, "I really like this yarn, but I'm not sure how much I'd need..." or "Sally's birthday is coming up, what kind of yarn might she like?" could be answered using this data. Test users were also very enthusiastic about seeing trends in their yarn consumption (and hoarding) habits and frequently ask for more ways to see the data and drill down to answer specific questions like "How could I have so much pink in my stash?" or "What are all of these 'no color specified' yarns? I should go back and tag them!"

You can view an instance running here: <http://blacktabi.elasticbeanstalk.com/>  (This instance might go down for a few minutes periodically because I'm still playing with new features and adding them as time permits)

Once the user has authenticated with Ravelry, they can enter a Raveler's username.  The app will display several charts:
- a pie chart showing projects by type
- a column chart showing how many projects used each standard weight of yarn
- a column chart for stash yarn weights.  
- a pie chart showing stash yarn by color (pie is colored to match each category)
After the charts are loaded, the user can click a slice of the project pie to re-load the project column chart showing only yarn used for projects of the specified type. Re-click the same pie to go back to the "All" view or click a new pie to see a different selection.  The three other charts are clickable as well - these bring up a pane with a list of projects and photos that belong to the selected category.  Each item in the pane takes the user to the corresponding page in Ravelry when clicked.


Technology
Grails 2.1.0 <http://grails.org/>
Dojo 1.6.1.7 <http://dojotoolkit.org/>
oauth 2.0.1 (grails Scribe plugin) <http://aiten.github.com/grails-oauth-scribe/>
Highcharts-2 <http://www.highcharts.com/>
Ravelry API

I wrote this app in Grails because it's the web app framework I'm familiar with from my day job.  I'm currently hosting it on Amazon EC2 with AWS Elastic Beanstalk running Tomcat 7 - this is mainly because I didn't really have a place to host a web app already and was interested in checking out how the AWS free tier of services work.  Elastic Beanstalk turns out to be pretty slick - I was impressed that I was able to sign up for an account, upload my war file, and had my app up and running in a matter of minutes.  I didn't care to persist anything for long amounts of time, so I didn't bother with a database implementation at this point.


High level technical overview:
The current implementation of the Yarn Store Enabler (YSE) authenticates with Ravelry, then brings up a dialog where the user can enter the name of his/her favorite Raveler.  From here, I make dual AJAX request to kick off my analysis and eventually populate the page.  I fire off two because the stash request is fast while the projects one is slow - this way I can populate the stash charts so the user has something to play with while waiting for the projects analysis to return.  In the calculate class I grab a list of the given user's projects from the Ravelry API, then iterate over them to gather data about the projects, project details, and patterns used. The stash line of code works similarly, but doesn't require extra database calls so it's faster.  Once I've aggregated the data I want to chart, I return it to the Javascript layer to render a few Highcharts charts and the object details boxes. The returned data structures are sort of large and complicated so that I can hold everything in the front end and keep the user experience clicking through charts fast and responsive.


Overview of Code

index.gsp and enable.gsp - basic HTML pages for the index page and main page.  These are pretty bare bones because most of the page is populated in javascript.

enablerJavascript.js - Javascript lives here.  There's text field for the Raveler's username and a button to call the get data functions that fire off AJAX calls for each type of data (I call them separately so the quicker charts will show up faster and give the user something to look at while slower charts calculate), a function to drive the chart creation so that I can easily re-render them with filtered data, and a couple chart-construction functions.  This file has grown larger and more unwieldy than I expected through adding new features; it's in need of a refactor and cleanup (I know the two pie chart functions could be factored into one generic function), but my day job is calling.

ProjectDataController.groovy - I have a single controller class, which currently has methods for getting an oauth token and a generic "get data" method that makes calls to the calculation service methods.  I'm likely going to refactor this a bit soon to have separate methods for calculating a Raveler's project and stash data so that those can be loaded separately from the front end.  (There are a few stubbed out sections for stash metrics I haven't implemented yet)

CalculateService.groovy - This class drives the calculations necessary to populate my charts.  For the project data that's currently available, I have a slightly hairy method that iterates over the Raveler's projects and also pulls up project details and pattern data.  I iterate through keeping track of total number of projects with given yarn weight characteristics for each pattern type.  One thing that's inefficient about this method is that the current implementation of the Ravelry API doesn't have bulk calls, so to really aggregate this data I end up making potentially a large number of calls in quick succession.  It also means this data takes a few minutes to calculate.  Since I know this is an expensive iteration, I do the aggregation for all pattern types at once and pass maps for each type back to the front end so that re-rendering the charts is speedy once I've parsed the data once and I don't have to make a bunch of unnecessary and slow API calls while the user plays with the interactive charts.

HttpService.groovy - I wrote this class to handle HTTP requests to the Ravelry API.  It's got a few methods that aren't in use yet that I have for future implementation.  So far I'm only making GET calls and handling cases that return errors by throwing an exception.  This either displays the barebones error on the main page if it's something unexpected or redirects to the index page if there's an authentication timeout.  The exception to this is 500 responses from Ravelry - I got some of those while testing various users, and rather than fail to load any charts whenever a project appears to be corrupted, I decided to just skip those cases and calculate based on everything else.

RavelryApi.java - extende Scribe's DefaultApi10a class with oauth configurations specific to Ravelry. I need to double check the Scribe documentation, but I think I should be able to add this to Scribe's repository so that Ravelry can be a built-in provider wit Scribe going forward.


Known Issues
* I currently store the auth token in the session, so once the session times out the user has to re-authenticate. I figured nobody is going to be using this for large amounts of time, so I focused attention elsewhere given a short dev timeline.
* The pages are ugly because I only gave them a cursory styling.
* Brute force API calls to aggregate data are slow and make a lot of likely unnecessary database calls.  A bulk API call would be nice here, but I didn't want to make a request for a highly specific API call for a toy project.
* in the detail browser, setting the row height value makes the header text disappear. This appears to be a known issue with dojo <http://bugs.dojotoolkit.org/ticket/12296>
* haven't handled every case where I could get weird data back from Ravelry - mostly handling these on a case-by-case basis as I encountered them for efficiency of what I could get done this week. Notably, the YarhHarlot has no stash and somewhere in her projects there's a failure I haven't tracked down yet.  Haven't handled the case where a username doesn't exist yet, either.

Further Development
I've been having fun with this project, so I'm bursting with ideas for further development
* it would be nice to have some analysis of how much yardage the Ravelery typically uses for certain types of projects. Average yardage for sport weight, average yardage for sport weight hats, etc. That would probably be the most useful feature for a fiber person standing in a yarn store trying to decide how much yarn to buy in an impulse purchase situation.
* add some analysis of fiber type?  Casey just added this to the API, and I haven't had time to do much with it. I decided to leave it out for now because it would require doing a whole extra set of API calls to grab yarn objects, and I didn't want to slow the calculations down or hammer the database any more than I already am.
* I think it would be really cool to take these metrics to make recommendations for people who either don't want to deal with chart reading or don't have enough knitting/crochet knowledge to interpret the data themselves. I'm picturing an app that someone who loves a Raveler could use to get guidelines for gift shopping.  Rather than trying to sift through the charts, the app could recommend that a Raveler particularly likes blue sport-weight, or that if someone wanted to buy the Raveler yarn to make a hat they'd do well to consider brown worsted merinos.  The user could go to a yarn shop armed with this information to make more educated gift buying decisions.   (For features like this, it would be useful for non-Ravelry users to be able to see agregated data in this style without needing an authentication.)
* Taken to much further extremes, the app could likely be made smart enough to take data from the user's prior projects, stash, and queue to recommend patterns and general yarn attributes for full-kit gifting recommendations.  Happy holidays!