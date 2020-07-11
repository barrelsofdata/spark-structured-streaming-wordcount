package com.barrelsofdata.sparkexamples

import org.apache.log4j.Logger
import org.apache.spark.sql.functions.{col, desc, from_json}
import org.apache.spark.sql.streaming.{OutputMode, StreamingQuery}
import org.apache.spark.sql.types.{StringType, StructType, TimestampType}
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

object Driver {

  val JOB_NAME: String = "Streaming Word Count"
  val LOG: Logger = Logger.getLogger(this.getClass.getCanonicalName)

  def run(spark: SparkSession, kafkaBroker: String, kafkaTopic: String): Unit = {
    import spark.implicits._
    spark.sparkContext.setLogLevel("ERROR")

    val inputSchema: StructType = new StructType().add("ts", TimestampType).add("str",StringType)

    val df:DataFrame = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers",kafkaBroker)
      .option("subscribe",kafkaTopic)
      .load()

    val data: Dataset[String] =  df
      .select(col("value").cast("STRING"))
      .select(from_json(col("value"), inputSchema).as("jsonConverted"))
      .select("jsonConverted.str").as[String]

    val words: Dataset[String] = data
      .map(WordCount.cleanData)
      .flatMap(WordCount.tokenize)
      .filter(_.nonEmpty)

    val wordFrequencies: DataFrame = words
      .groupBy(col("value")).count()
      .toDF("word", "frequency")

    val query: StreamingQuery = wordFrequencies
      .orderBy(desc("frequency"))
      .writeStream
      .format("console")
      .outputMode(OutputMode.Complete())
      .start()

    query.awaitTermination()
  }

  def main(args: Array[String]): Unit = {
    if(args.length != 2) {
      println("Invalid usage")
      println("Usage: spark-submit --master <local|yarn> spark-structured-streaming-wordcount-1.0.jar <kafka_broker> <kafka_topic>")
      LOG.error(s"Invalid number of arguments, arguments given: [${args.mkString(",")}]")
      System.exit(1)
    }
    val spark: SparkSession = SparkSession.builder().appName(JOB_NAME).getOrCreate()

    run(spark, args(0), args(1))

  }

}
