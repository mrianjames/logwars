#!/bin/bash
#To run this, build the logwars project to make the jar - mvn clean install.
#Then go into the scripts folder and run this script.
if [ "$(expr substr $(uname -s) 1 6)" == "CYGWIN" ];then
 set -o igncr
 export TEST_JARS="../lib/log4j-slf4j-impl-2.0-rc1.jar;../lib/log4j-api-2.0-rc1.jar;../lib/log4j-core-2.0-rc1.jar"
else
 export TEST_JARS="../lib/log4j-slf4j-impl-2.0-rc1.jar:../lib/log4j-api-2.0-rc1.jar:../lib/log4j-core-2.0-rc1.jar"
fi
export TEST_NAME="Log4J2"
export TEST_OPTIONS=""

./do_test.sh $1

