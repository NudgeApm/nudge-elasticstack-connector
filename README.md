
# Nudge Elastic Stack connector

## Overview

The Nudge-ElasticStack Connector is a daemon that let you integrate your applications performance measures analyzed by [Nudge APM](https://www.nudge-apm.com/) into your Elastic Stack.

## Live demo
To view a live demo follow this link :
[bit.ly/2yZwxge](http://bit.ly/2yZwxge)</a>

## Requirements
1. A [Nudge APM](https://www.nudge-apm.com/) api token
2. Elastic 2.3, 2.4 or 5.x
3. Kibana 4.5.0 or Kibana 5.x
4. Java >= 1.7 (Open JDK and Oracle JVM have been tested)

## Getting started
First download and unpack our archive.

```
wget https://github.com/NudgeApm/nudge-elasticstack-connector/releases/download/v1.5.0/nudge-elasticstack-plugin-1.5.0.zip
unzip -d nudge-elasticstack-plugin nudge-elasticstack-plugin-1.5.0.zip 
```

Then edit the properties file and set your own properties.
These are the mandatory properties you have to specify :


|Property|Value|
|-|-|
|nudge.api.token|[Nudge APM](https://www.nudge-apm.com/) API authentication token platform|
|nudge.apps.ids|Your application token in [Nudge API](https://monitor.nudge-apm.com/api-doc/)|
|elastic.index|The name of the Elastic index you want the plugin to write to|
|output.elastic.hosts|Elastic hosts (default http://localhost:9200)|

Finally start the service :

```
java -jar nudge-elasticstack-connector.jar -startDaemon
```

The plugin is now fetching live data from [nudge-apm.com](https://www.nudge-apm.com/) and writing them to your Elastic.
After running the connector, you can easily set up an initial Kibana dashboard using the shell script provided in the archive : `kibana_dashboards_init.sh`.

```
cd kibana_dashboards_init
./kibana_dashboards_init.sh import
```

For more information about this script, read the [related documentation page](https://github.com/NudgeApm/nudge-elasticstack-connector/blob/master/script/kibana_dashboards_init/README.md).

## Documentation

Analysed fields that can be used in a Kibana search :

| Field Name          | Type          | Description                                                                 |
| ------------------- |:-------------:| --------------------------------------------------------------------------- |
| appId               | String        | Id of an application, defined in the properties file of a Nudge APM Agent   |
| appName             | String        | Name of an application configured in Nudge APM portal                       |
| host                | String        | Host code detected by a Nudge APM Agent                                     |
| hostname            | String        | Host name configured in Nudge APM portal                                    |
| layer_jaws_name     | String        | Name of a JAX-WS webservice called in a transaction                         |
| layer_jms_name      | String        | Name of a JMS queue or topic called in a transaction                        |
| layer_sql_name      | String        | Code of a SQL query called in a transaction                                 |
| mbean_attributename | String        | Name of a Java JMX MBean attribute                                          |
| mbean_name          | String        | Name of a Java JMX MBean attribute                                          |
| sql_code            | String        | SQL query code                                                              |
| transaction_name    | String        | Name of a transaction configured in Nudge APM portal                        |
| transactionId       | String        | Code of a transaction, used if the transaction_name is not configured       |
| type                | String        | Type of a elasticsearch document : GEO_LOC, JAX-WS, MBEAN, SQL, TRANSACTION |


Visit [www.nudge-apm.com](http://bit.ly/2u46AKu) for more information on Nudge APM Integrations.
