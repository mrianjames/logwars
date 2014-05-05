#!/bin/bash
#To run this, build the logwars project to make the jar - mvn clean install.
#Then go into the scripts folder and run this script.
if [ "$(expr substr $(uname -s) 1 6)" == "CYGWIN" ];then
 set -o igncr
 export TEST_JARS="../lib/slf4j-log4j12-1.7.6.jar;../lib/log4j-1.2.17.jar"
else
 export TEST_JARS="../lib/slf4j-log4j12-1.7.6.jar:../lib/log4j-1.2.17.jar"
fi
export TEST_NAME="Log4J"
export TEST_OPTIONS=""

./do_test.sh $1

