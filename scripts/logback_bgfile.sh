#!/bin/bash
#To run this, build the logwars project to make the jar - mvn clean install. 
#Then go into the scripts folder and run this script.
if [ "$(expr substr $(uname -s) 1 6)" == "CYGWIN" ];then
 set -o igncr
 export TEST_JARS="../lib/logback-core-1.1.1.jar;../lib/logback-classic-1.1.1.jar"
else 
 export TEST_JARS="../lib/logback-core-1.1.1.jar:../lib/logback-classic-1.1.1.jar"
fi
export TEST_NAME="LogbackAsync"
export TEST_OPTIONS="-Dlogback.configurationFile=../properties/logback_asyncfile.xml"

./do_test.sh $1
