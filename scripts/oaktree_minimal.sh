#!/bin/bash
#To run this, build the logwars project to make the jar - mvn clean install.
#Then go into the scripts folder and run this script.
if [ "$(expr substr $(uname -s) 1 6)" == "CYGWIN" ];then
 set -o igncr
fi

export TEST_JARS="../lib/slf4j-oaktree-1.0.18.jar"
export TEST_OPTIONS="-Doaktree.logging.config.file=../properties/oaktree.minimal.logging.properties"
export TEST_NAME="OAKTREE_MINIMAL"


./do_test.sh $1
