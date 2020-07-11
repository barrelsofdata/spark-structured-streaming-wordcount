# Spark Structured Streaming Word Count
This is a project detailing how to write a streaming word count program in Apache Spark using Structured Streaming. The related blog post can be found at [https://www.barrelsofdata.com/spark-structured-streaming-word-count](https://www.barrelsofdata.com/spark-structured-streaming-word-count)

## Build instructions
From the root of the project execute the below commands
- To clear all compiled classes, build and log directories
```shell script
./gradlew clean
```
- To run tests
```shell script
./gradlew test
```
- To build jar
```shell script
./gradlew shadowJar
```
- All combined
```shell script
./gradlew clean test shadowJar
```

## Run
Ensure your local hadoop cluster is running ([hadoop cluster tutorial](https://www.barrelsofdata.com/apache-hadoop-pseudo-distributed-mode)) and start two kafka brokers ([kafka tutorial](https://www.barrelsofdata.com/apache-kafka-setup)).
- Create kafka topic
```shell script
kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 2 --partitions 2 --topic streaming-data
```
- Start streaming job
```shell script
spark-submit --master yarn --deploy-mode cluster build/libs/spark-structured-streaming-wordcount-1.0.jar <KAFKA_BROKER> <KAFKA_TOPIC>
Example: spark-submit --master yarn --deploy-mode client build/libs/spark-structured-streaming-wordcount-1.0.jar localhost:9092 streaming-data
```
- You can feed simulated data to the kafka topic
- Open new terminal and run the shell script located at src/test/resources/dataProducer.sh
- Produces the following json structure every 1 second: {"ts":1594307307,"str":"This is an example string"}
```shell script
cd src/test/resources
./dataProducer.sh localhost:9092 streaming-data
```