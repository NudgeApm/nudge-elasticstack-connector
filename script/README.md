###***kibana_dashboard_init.sh***###
-----------------------


Within the connector there is a script called : **kibana_dashboard_init.sh** in the folder script.
It directly imports  visualizations and dashboards in you Kibana. They are based on the data of the software of accessible demonstration of Nudge after your registration on the site. 
So you can see how to build your own visualisations, dashboards very easily.


####***Requirements ***####
-----------------------
Before anything else, you have to execute the Nudge plugin. 
It is by this way that the script can get back the data of the application of Nudge. 

####***Configuration***####
-----------------------
 If you had changed the field " elastic.index " by another name in the properties file of the Nudge plugin, put again "nudge.index". 

The only configuration which you can modify in the script without impacting its using  is the hosts of elasticSearch.

####***Getting started***####
-----------------------
Without argument :

	 $ ./kibana_dashboard_init.sh 

You access to the help, and view all arguments of the script that you can use.

If you have an error message like : 


	 $ bash : kibana_dashboard_init.sh : Permission denied
do this :


	 $ chmod +x kibana_dashboard_init.sh
	 $ ./kibana_dashboard_init.sh

----

#####**Import**

	 $ ./kibana_dashboard_init.sh import

All visualisations and dashboards  will import in your kibana.

----
#####**Suppress the import**

It is possible  to delete visualisations, dashboards and index-pattern generated.
For that purpose, you have to launch the script with the parameters presented in the help.

	  $ kibana_dashboard_init.sh delete_all    
      $ kibana_dashboard_init.sh delete_visu
      $ kibana_dashboard_init.sh delete_dash

**************
**Have a good execution !** 