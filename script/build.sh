#!/bin/bash

# requirements : maven, zip, tar, gzip
# must be lauched from project root directory

MAVEN="mvn"
TEMP="/tmp"

# clean
$MAVEN clean
