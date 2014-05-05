#!/bin/bash
#To run this, build the logwars project to make the jar - mvn clean install.
#Then go into the scripts folder and run this script.
if [ "$(expr substr $(uname -s) 1 6)" == "CYGWIN" ];then
 set -o igncr
fi
THREADS=$1
if [ $# -lt 1 ]; then
 THREADS=20
fi
rm ../logs/result.txt
rm  ../logs/*.txt
echo `date` >> ../logs/result.txt
echo `uname -a` >> ../logs/result.txt
echo "**************************"
echo SYNC TESTS
echo "**************************"
echo
echo "Running OakTree Console Test with threads ${THREADS}"
./oaktree_console.sh ${THREADS} >> ../logs/oaktree_console.txt 2>&1
echo "Running OakTree Minimal Test with threads ${THREADS}"
./oaktree_minimal.sh ${THREADS} >> ../logs/oaktree_minimal_output.txt 2>&1
echo "Running OakTree File Test with threads ${THREADS}"
./oaktree_file.sh ${THREADS} >> ../logs/oaktree_file_output.txt 2>&1
echo "Running JDK Test with threads ${THREADS}"
./jdk.sh ${THREADS} >> ../logs/jdk.txt 2>&1
echo "Running LogBack Test with threads ${THREADS}"
./logback.sh ${THREADS} >> ../logs/logback.txt 2>&1
echo "Running Log4J Test with threads ${THREADS}"
./log4j.sh ${THREADS} >> ../logs/log4j.txt 2>&1
echo "Running Log4J2 Test with threads ${THREADS}"
./log4j2.sh ${THREADS} >> ../logs/log4j2.txt 2>&1

#ASYNC ONES
echo "**************************"
echo ASYNC TESTS
echo "**************************"
echo 

echo "Running LogBackASYNCFILE Test with threads ${THREADS}"
./logback_bgfile.sh ${THREADS} >> ../logs/logback_async.txt 2>&1

echo "Running Log4JASYNCFILE Test with threads ${THREADS}"
./log4j_async.sh ${THREADS} >> ../logs/log4j_async.txt 2>&1

echo "Running Log4J2ASYNCFILE Test with threads ${THREADS}"
./log4j2_async.sh ${THREADS} >> ../logs/log4j2_async.txt 2>&1

echo "Running OaktreeBGFile Test with threads ${THREADS}"
./oaktree_bgfile.sh ${THREADS} >> ../logs/oaktree_bgfile_output.txt 2>&1


cat ../logs/result.txt
