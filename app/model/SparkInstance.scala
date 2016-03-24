package model

import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by Jake on 24/03/2016.
  */
object SparkInstance {
  val conf = new SparkConf().setAppName("Sentitweet").setMaster("local")
  val sc = new SparkContext(conf)
}
