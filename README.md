Nudge Elastic Stack plugin
=

Overview
-

The Nudge ElasticStack Plugin is a very simple daemon connector that let you integrate your applications performance measures analyzed by Nudge APM into your Elastic stack.

Requirements
-
1. A Nudge APM account (login, password) and the token of the app (allowed for this account) you want to view in Kibana. You can find this token in the application settings screen.
2. Elastic >= 2.3.
3. Kibana >= 4.5.0.
4. Java >= 1.7 (Open JDK and Oracle JVM have been tested).

Getting started
-

First download and unpack our archive ...

	$ wget ...
	$ tar -zxvf ...

Then edit the properties file and set your own properties.
These are the mandatory properties you have to specify :

| Property       | Value                                                       |
|----------------|-------------------------------------------------------------|
|`nudge.login`   |Login to Nudge APM platform                                  |
|`nudge.password`|Password to Nudge APM platform                               |
|`nudge.apps.ids`|Your application token                                       |
|`elastic.index` |The name of the Elastic index you want the plugin to write to|
|`elastic.output`|Elastic hosts (default http://localhost:9200)                |

Finally start the service.

	$ java -jar nudge-elastocstack-plugin.jar -startDaemon

The plugin is now fetching live data from Nudge APM and writing them to your Elastic.
You can easily set up a initial Kibana dashboard using the shell script provided in the archive : `kibana_dashboard_init.sh`.

	$ ./kibana_dashboard_init.sh import

<<<<<<< HEAD
☞ **On the side from Nudge**

To use the plugin of Nudge, it is necessary to be registered as user on the site of Nudge and possess :

	- Your credentials
	- ID of the application

☞ **On the side from Elastic stack**

It is essential that you have :

	- ElasticSearch (version 2.3.X)
	- Kibana (version 4.5.X)

You can download it at this address: <https://www.elastic.co/>



☞ **on the side of work environment**

	- Java 7 or superior


###***Configuration***###
-----------------------

You just have to configure your propertie file to use the plugin.

	- nudge.login:  you got it when you are register on Nudge site
	- nudge.password : you got it like the nudge login
	- nudge.app.ids : you got it when you request the Nudge api
	- elastic.index :  it's the name of your index on elasticSearch
	- elastic.output : it's your elastic port



###***Launch the plugin***###
-----------------------

You have just to do this command in your terminal

	- $ java nudge-elasticstack-plugin.jar –StartDeamon

*****
 **Congratulations !! The plugin works ! Go on your elasticSearch, your index is created ! Go on your Kibana and start to graph !**

----
####**What ? You want an example ? You want to know what can you do with the plugin ? You want directly import visualisations and dashboards in your kibana easier?
We have the solution : Use the script :** ####

****


**_The Script_ : kibana_dashboard_init.sh**
----------

Within the plugin, there is a script called : **kibana_dashboard_init.sh**
The script serves as demonstration and makes it possible as well as possible to apprehend what it is possible to generate and to realize thanks to the plugin.
Thus, it directly imports the corresponding visualizations and dashboards. They are based on the data of the software of accessible demonstration of Nudge after your registration on the site.




###***technical prerequisites***###
-----------------------
Before anything else, you have to execute the plugin
It is indeed by its way that the script can get back the data of the application of Nudge.

###***Configuration***###
-----------------------
✔ If you had changed the field " elastic.index " by another name in the properties file of the plugin, put again "nudge.index".

✔ The only configuration which you can modify without impacting on the immediate use of the plugin is the change of port of elasticSearch. Just like for the plugin, it listens to the default port 9200 (localhost).


###***Launch the script***###
-----------------------

☞ **import**

Open your terminal and put you in the root of the file containing the file .sh and execute the script thanks to this command :


	 $ ./kibana_dashboard_init.sh

If you have an error message like :


	 $ bash : ./kibana_dashboard_init.sh: Permission denied
do this :


	 $ chmod +x kibana_dashboard_init.sh
	 $ ./kibana_dashboard_init.sh

The help appear and if you do :



	 $ ./kibana_dashboard_init.sh import
All visualisations and dashboards which will import !

☞ **Suppress the import**

it is possible  to delete imports from your elasticSearch and your Kibana.
For that purpose, you have to lauchn the script with the parameters presented in the help.


	  $ kibana_dashboard_init.sh delete_all
      $ kibana_dashboard_init.sh delete_visu
      $ kibana_dashboard_init.sh delete_dash

**************
**Have a good execution !**
=======
For more informations about this script, read the related documentation page.
