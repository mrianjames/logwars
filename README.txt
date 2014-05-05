Welcome to LogWars. This is a log implementation comparison for low latency use cases we do every day.
We care not of features we just dont use, performance round logging is key.

To Build

mvn clean install

To Run

First build as above. This will create the jars in the target folder we use. 
Then goto scripts folder and run the individual tests:
e.g. jdk.sh 20 (run jdk impl test with 20 threads in the concurrent test).

or run the overall test:
test.sh - this runs all tests with 20 threads in concurrent section and reports results to screen and ../logs/results.txt file.

If you get:
Error: Could not find or load main class com.logwars.LogTest
It suggests you havent built the latest log test jar as above.