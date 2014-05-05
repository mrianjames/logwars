echo TESTNAME:${TEST_NAME}
echo TEST_OPTIONS:${TEST_OPTIONS}
echo TEST_JARS:${TEST_JARS}
#ALLOCATIONS="-javaagent:../lib/allocation.jar -Drecord.allocations=false"
ALLOCATIONS=
LOG_WARS_JAR="../target/com.oaktree.logwars-0.0.1-SNAPSHOT.jar"
OPTIONS="-Xms1024M -Xmx1024M ${TEST_OPTIONS}"
if [ "$(expr substr $(uname -s) 1 6)" == "CYGWIN" ];then
    CP="../properties;../lib/slf4j-api-1.7.6.jar;${LOG_WARS_JAR};${TEST_JARS};../lib/allocation.jar" 
else 
   CP="../properties:../lib/slf4j-api-1.7.6.jar:${LOG_WARS_JAR}:${TEST_JARS}:../lib/allocation.jar"
fi
JAVA="java ${ALLOCATIONS} -cp ${CP} ${OPTIONS} com.logwars.LogTest ${TEST_NAME} $1"
echo Java:${JAVA}
${JAVA}
