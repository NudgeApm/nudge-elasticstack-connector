
#**Nudge Elastic Stack connector**#

***
###**Overview**###
***

The Nudge ElasticStack Plugin is a very simple daemon connector that let you integrate your applications performance measures analyzed by [Nudge APM](https://www.nudge-apm.com/) into your Elastic stack.
***

###**Live demo**###
To view a live demo follow this link :
[http://kibana.nudgeapm.io](http://kibana.nudgeapm.io/app/kibana#/dashboard/nudgeapm_PetclinicOverviewDashboardTransaction)

###**Requirements**###
***
1. A [Nudge APM](https://www.nudge-apm.com/) account (login, password) and the token of the app (allowed for this account) you want to view in Kibana. You can find this token in the application settings screen.
2. Elastic >= 2.3.
3. Kibana >= 4.5.0.
4. Java >= 1.7 (Open JDK and Oracle JVM have been tested).

###**Getting started**###
***
First download and unpack our archive.

	$ wget https://github.com/NudgeApm/nudge-elasticstack-connector/releases/download/v1.1.0/nudge-elasticstack-connector-1.1.0.zip
	$ unzip nudge-elasticstack-connector-1.1.0.zip

Then edit the properties file and set your own properties.
These are the mandatory properties you have to specify :


| Property       | Value                                                       |
|----------------|-------------------------------------------------------------|
|nudge.login   |Login to [Nudge APM](https://www.nudge-apm.com/) platform                                  |
|nudge.password|Password to [Nudge APM](https://www.nudge-apm.com/) platform                               |
|nudge.apps.ids|Your application token in [Nudge API](https://monitor.nudge-apm.com/api-doc/)                                      |
|elastic.index |The name of the Elastic index you want the plugin to write to|
|output.elastic.hosts|Elastic hosts (default http://localhost:9200)                |

Finally start the service.

	$ java -jar nudge-elasticstack-connector-1.1.0.jar -startDaemon

The plugin is now fetching live data from [Nudge APM](https://www.nudge-apm.com/) and writing them to your Elastic.
You can easily set up a initial Kibana dashboard using the shell script provided in the archive : `kibana_dashboard_init.sh`.

	$ ./kibana_dashboard_init.sh import

For more informations about this script, read the related documentation page.

