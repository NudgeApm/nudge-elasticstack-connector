
#**Nudge Elastic Stack connector**#

##Overview

The Nudge ElasticStack Plugin is a very simple daemon connector that let you integrate your applications performance measures analyzed by [Nudge APM](https://www.nudge-apm.com/) into your Elastic stack.

##Live demo
To view a live demo follow this link :
[http://kibana.nudgeapm.io](
http://kibana.nudgeapm.io/app/kibana#/dashboard/nudgeapm_TransactionDashboard?_g=(refreshInterval:(display:'5%20seconds',pause:!f,section:1,value:5000),time:(from:now-15m,mode:quick,to:now))&_a=(filters:!(),options:(darkTheme:!t),panels:!((col:5,id:nudgeapm_ResponsetimeLayer,panelIndex:1,row:1,size_x:8,size_y:3,type:visualization),(col:1,id:nudgeapm_CountTransaction,panelIndex:5,row:4,size_x:12,size_y:3,type:visualization),(col:1,id:nudgeapm_TransactionEvolution,panelIndex:7,row:7,size_x:8,size_y:2,type:visualization),(col:9,id:nudgeapm_TransactionResponseTimeNumber,panelIndex:8,row:7,size_x:4,size_y:2,type:visualization),(col:1,id:nudgeapm_SqlResponseTimeEvolution,panelIndex:9,row:9,size_x:8,size_y:2,type:visualization),(col:9,id:nudgeapm_SqlResponseTimeNumber,panelIndex:13,row:9,size_x:4,size_y:2,type:visualization),(col:9,id:nudgeapm_TopTransactionsCount,panelIndex:24,row:11,size_x:4,size_y:4,type:visualization),(col:1,id:nudgeapm_TopTransactions,panelIndex:26,row:11,size_x:8,size_y:4,type:visualization),(col:1,id:nudgeapm_TopSql,panelIndex:27,row:15,size_x:8,size_y:4,type:visualization),(col:9,id:nudgeapm_TopSqlCount,panelIndex:28,row:15,size_x:4,size_y:4,type:visualization),(col:1,id:nudgeapm_Navigation,panelIndex:29,row:1,size_x:4,size_y:3,type:visualization)),query:(query_string:(analyze_wildcard:!t,query:'*')),title:'Transaction%20Dashboard',uiState:(P-1:(vis:(legendOpen:!t)),P-5:(vis:(legendOpen:!f)),P-7:(vis:(legendOpen:!f)),P-9:(vis:(legendOpen:!f)))))

##Requirements
1. A [Nudge APM](https://www.nudge-apm.com/) account (login, password) and the token of the app (allowed for this account) you want to view in Kibana. You can find this token in the application settings screen.
2. Elastic >= 2.3.
3. Kibana >= 4.5.0.
4. Java >= 1.7 (Open JDK and Oracle JVM have been tested).

##Getting started
First download and unpack our archive.

```
$ wget https://github.com/NudgeApm/nudge-elasticstack-connector/releases/download/v1.1.0/nudge-elasticstack-connector-1.1.0.zip
$ unzip nudge-elasticstack-connector-1.1.0.zip
```

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

```
$ java -jar nudge-elasticstack-connector-1.1.0.jar -startDaemon
```

The plugin is now fetching live data from [Nudge APM](https://www.nudge-apm.com/) and writing them to your Elastic.
After running the connector, you can easily set up an initial Kibana dashboard using the shell script provided in the archive : `kibana_dashboard_init.sh`.

```
$ ./script/kibana_dashboard_init.sh import
```

For more information about this script, read the [related documentation page](https://github.com/NudgeApm/nudge-elasticstack-connector/blob/master/script/kibana_dashboards_init/README.md).

