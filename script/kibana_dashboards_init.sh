#!/bin/sh
# Author : Sarah Bourgeois
# Description : import visualizations and dashboards directly on your Kibana thanks to the Nudge connector.

#Elastic Stack parameters
ELASTICSEARCH_HOST="http://localhost:9200/"
NAME_ELASTIC_INDEX=".kibana"
#Internal script command
CURL=curl
DIR=dash

echo "\n"
echo "-------------------------------------------------"
echo "              kibana_dashboard_init              "
echo "-------------------------------------------------"
echo "\n"

# Help to use correctly arguments
help_command () {
echo  "help :
      import       ==> to import vizualisation and dashboards from Nudge data
      delete_all   ==> to delete Nudge vizualisations and dashboards which are in your .kibana:
      delete_visu  ==> to delete vizualisations one per one
      delete_dash  ==> to delete dashboards one per one "
      echo "\n"
}

# Import Nudge vizualisations, Nudge index-pattern and Nudge dasboards
import () {
  echo "Processing... "
  echo "Loading to $ELASTICSEARCH_HOST in $NAME_ELASTIC_INDEX ... "


for file in $DIR/visualization/*.json
do
    name=`basename $file .json`
    echo "Loading visualization called $name: "
    curl -XPUT $ELASTICSEARCH_HOST/$NAME_ELASTIC_INDEX/visualization/$name -d @$file ||exit 1
    echo "\n"
done

for file in $DIR/index-pattern/*.json
do
   name=`awk '$1 == "\"title\":" {gsub(/"/, "", $2); print $2}' $file`
    echo "Loading index pattern $name:"
   $CURL -XPUT $ELASTICSEARCH_HOST/$NAME_ELASTIC_INDEX/index-pattern/$name  \
      -d @$file
    echo "\n"
done

for file in $DIR/dashboards/*.json
do
    name=`basename $file .json`
    echo "Loading dashboard $name:"
    $CURL -XPUT $ELASTICSEARCH_HOST/$NAME_ELASTIC_INDEX/dashboard/$name \
        -d @$file
    echo
done
echo "\n"
}

# Delete all : vizualisations, index-pattern and dasboards
delete_all () {
for file in $DIR/visualization/*.json
do
    name=`basename $file .json`
    echo "Supressing visualization called $name: "
    curl -XDELETE $ELASTICSEARCH_HOST/$NAME_ELASTIC_INDEX/visualization/$name -d @$file ||exit 1
    echo "\n"
done

for file in $DIR/dashboards/*.json
do
    name=`basename $file .json`
    echo "Supressing dashboards called $name: "
    curl -XDELETE $ELASTICSEARCH_HOST/$NAME_ELASTIC_INDEX/dashboards/$name -d @$file ||exit 1
    echo "\n"
done

for file in $DIR/index-pattern/*.json
do
    name=`basename $file .json`
    echo "Supressing index-pattern called $name: "
    curl -XDELETE $ELASTICSEARCH_HOST/$NAME_ELASTIC_INDEX/index-pattern/$name -d @$file ||exit 1
    echo "\n"
done
}

# Delete vizualisation one per one
delete_visu () {
echo "List of Nudge vizualisations : "
echo $DIR/visualization/*.json "\n"
for file in $DIR/visualization/*
do
read -p 'Which vizualisation do you want to delete ? (example : nudgeapm_ResponsetimeLayers)  ' name
    $CURL -XDELETE $ELASTICSEARCH_HOST/$NAME_ELASTIC_INDEX/visualization/$name \
        -d @$file
    echo "\n"
done
}

# Delete dashboards one per one
delete_dash() {
  echo "List of Nudge dashboards : "
  echo $DIR/dashboards/*.json "\n"
for file in $DIR/dashboards/*.json
do
read -p 'Which dashboard do you want to delete ? (example : nudgeapm_petclinicDashboards)  ' name
    curl -XDELETE $ELASTICSEARCH_HOST/$NAME_ELASTIC_INDEX/dashboards/$name -d @$file ||exit 1
    echo  "\n"
done
}

#Arguments to use the Nudge script
if [ $1 = '' ];
then
help_command
fi

if [ $1 = 'import' ];
then
import
fi

if [ $1 = 'delete_all' ];
then
delete_all
fi

if [ $1 = 'delete_visu' ];
then
delete_visu
fi

if [ $1 = 'delete_dash' ];
then
delete_dash
fi