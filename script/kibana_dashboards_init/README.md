# kibana_dashboard_init.sh

It directly imports visualizations and dashboards in you Kibana, and will display you imported Nudge data from your monitored application.

From these visualizations and dashboards, you can easily build your own.

## Requirements
Before anything else, you have to execute the Nudge plugin, in order to import Nudge probe data.

## Configuration

| Tables                  | Value                                                               |
| ----------------------- | ------------------------------------------------------------------- |
| ELASTICSEARCH_HOST      | URL of your elasticsearch host (by default _http://localhost:9200_) |


The only configuration which you can modify in the script without impacting its using is the hosts of elasticSearch.

## Getting started
Without argument, the help section is displayed.

    ./kibana_dashboard_init.sh


## Import

    ./kibana_dashboard_init.sh import

All Nudge visualisations and dashboards will be imported in your Kibana.

## Remove the import

It is possible to delete visualisations, dashboards and index-pattern generated.
For this purpose, you have to launch the script with the parameters presented in the help.

    kibana_dashboard_init.sh delete_all
    kibana_dashboard_init.sh delete_visu
    kibana_dashboard_init.sh delete_dash

## Restrictions
If you have changed the Nudge connector property **elastic.index** by an another, the import will not work.
