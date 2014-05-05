#!/bin/bash
#To run this, build the logwars project to make the jar - mvn clean install.
#Then go into the scripts folder and run this script.
if [ "$(expr substr $(uname -s) 1 6)" == "CYGWIN" ];then
 set -o igncr
fi

#To run this, build the logwars project to make the jar - mvn clean install. 
#Then go into the scripts folder and run this script.
export TEST_JARS="../lib/slf4j-jdk14-1.7.6.jar"
export TEST_OPTIONS="-Djava.util.logging.config.file=../properties/jdk.logging.properties"
export TEST_NAME="JDK"

./do_test.sh $1
