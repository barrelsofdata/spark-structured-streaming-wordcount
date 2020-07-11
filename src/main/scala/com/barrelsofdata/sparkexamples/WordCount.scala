package com.barrelsofdata.sparkexamples

import org.apache.log4j.Logger

object WordCount extends Serializable {

  val LOG: Logger = Logger.getLogger(this.getClass.getCanonicalName)

  def cleanData(line: String): String = {
    line
      .toLowerCase
      .replaceAll("[,.]"," ")
      .replaceAll("[^a-z0-9\\s-]","")
      .replaceAll("\\s+"," ")
      .trim
  }

  def tokenize(line: String): List[String] = {
    line.split("\\s").toList
  }

  def keyValueGenerator(word: String): (String, Int) = {
    (word, 1)
  }

}
