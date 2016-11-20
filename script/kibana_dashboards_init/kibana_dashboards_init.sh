#!/bin/bash
# Author : Sarah Bourgeois
# Description : import Nudge Dashboards and Visualizations in your Kibana.

#Elastic Stack parameters
ELASTICSEARCH_HOST="http://localhost:9200"

#Internal script command
NAME_ELASTIC_INDEX=".kibana"
DIR=dash
NUDGE_INDEX_PATTERN="nudge-*"
NUDGE_INDEX_PATTERN_FILE="$DIR/index-pattern/nudgeapm_indexPattern.json"

printf "\n"
echo "-------------------------------------------------"
echo "              kibana_dashboard_init              "
echo "-------------------------------------------------"
printf "\n"

# Help to use correctly arguments
help_command () {
  echo  "help :
  import                ==> import visualizations and dashboards in your Kibana
  import_index_pattern  ==> import on Nudge index pattern in your Kibana
  delete_all            ==> delete Nudge APM visualizations and dashboards from your \".kibana\" index
  delete_visu           ==> delete visualization one by one
  delete_dash           ==> delete dashboard one by one"
  printf "\n"
}

curl_delete() {
  echo "Delete $1 with id \"$2\": "
  echo "curl -XDELETE $CURL_OPTS $ELASTICSEARCH_HOST/$NAME_ELASTIC_INDEX/$1/$2\?pretty"
  curl -XDELETE $CURL_OPTS $ELASTICSEARCH_HOST/$NAME_ELASTIC_INDEX/$1/$2\?pretty
  echo
}

list_object() {
echo "List of Nudge $1s : "
  for file in $DIR/$1/*.json
  do
    echo $file
  done
}

import_index_pattern() {
  echo "****************************"
  echo "*** Index-pattern import ***"
  echo "****************************"
    curl -XPUT $CURL_OPTS $ELASTICSEARCH_HOST/$NAME_ELASTIC_INDEX/index-pattern/$NUDGE_INDEX_PATTERN?pretty -d @$NUDGE_INDEX_PATTERN_FILE
}

# Import Nudge visualizations, Nudge index-pattern and Nudge dasboards
import () {
  echo "Processing... "
  echo "Loading to $ELASTICSEARCH_HOST in $NAME_ELASTIC_INDEX ... "
  printf "\n"

  import_index_pattern

  echo "*****************************"
  echo "*** Visualizations import ***"
  echo "*****************************"
  for file in $DIR/visualization/*.json
  do
    id=`basename $file .json`
    echo "\"$id\" import response:"
    curl -XPUT $CURL_OPTS $ELASTICSEARCH_HOST/$NAME_ELASTIC_INDEX/visualization/$id?pretty -d @$file
    printf "\n"
  done

  echo "*************************"
  echo "*** Dashboards import ***"
  echo "*************************"
  for file in $DIR/dashboard/*.json
  do
    id=`basename $file .json`
    echo "\"$id\" import response:"
    curl -XPUT $CURL_OPTS $ELASTICSEARCH_HOST/$NAME_ELASTIC_INDEX/dashboard/$id?pretty -d @$file
    echo
  done
  printf "\n"
}

# Delete all : visualizations, dashboards and index-pattern
delete_all () {
  for file in $DIR/visualization/*.json
  do
    id=`basename $file .json`
    curl_delete 'visualization' $id
  done

  for file in $DIR/dashboard/*.json
  do
    id=`basename $file .json`
    curl_delete 'dashboard' $id
  done

  id="nudge-*"
  curl_delete 'index-pattern' $id
}

# Delete visualization one by one
delete_visu() {
  list_object visualization
  read -p "Type the name of the dashboard to delete (example : nudgeapm_CpuLoad) : " visuId
  echo
  until [[ -z $visuId ]] ;do
    curl_delete 'visualization' $visuId
    read -p "Delete another visualization ? [y/n] " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        read -p "Type the name of the visualization to delete (example : nudgeapm_CpuLoad) : " visualization
    else
        break
    fi
  done
}

# Delete dashboards one by one
delete_dash() {
  list_object dashboard
  read -p "Type the name of the dashboard to delete (example : nudgeapm_TransactionDashboard) : " dashId
  echo
  until [[ -z $dashId ]] ;do
    curl_delete 'dashboard' $dashId
    read -p "Delete another dashboard ? [y/n] " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        read -p "Type the name of the dashboard to delete (example : nudgeapm_TransactionDashboard) : " dashId
    else
        break
    fi
  done
}

#Arguments to use the Nudge script
if [[ $1 = '' ]];
then
  help_command
fi

if [[ $1 = 'import' ]];
then
  import
fi

if [[ $1 = 'import_index_pattern' ]];
then
  import_index_pattern
fi

if [[ $1 = 'delete_all' ]];
then
  delete_all
fi

if [[ $1 = 'delete_visu' ]];
then
  delete_visu
fi

if [[ $1 = 'delete_dash' ]];
then
  delete_dash
fi
