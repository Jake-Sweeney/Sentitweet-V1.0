package model

import java.io.{BufferedReader, InputStreamReader, InputStream}
import java.net.URL

import play.api.Play

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Jake on 24/03/2016.
  */
class SampleDataLoader(fileName: String) {

  val tweets = scala.collection.mutable.MutableList[Tweet]()

  def loadData(): List[Tweet] = {
    val url = new URL("http://jsweeney.xyz/sampledata/" + fileName)
    println(url)
    val in = new BufferedReader(new InputStreamReader(url.openStream))
    val buffer = new ArrayBuffer[String]()
    var inputLine = in.readLine
    while (inputLine != null) {
      if (!inputLine.trim.equals("")) {
        buffer += inputLine.trim
      }
      println("INPUT : " + inputLine)
      inputLine = in.readLine
    }
    in.close

    for(tweet <- buffer) {
      val currentLineElements = tweet.split(":;:")
      tweets += new Tweet(
        currentLineElements(0).toLong,
        currentLineElements(1),
        currentLineElements(2),
        currentLineElements(3),
        currentLineElements(4),
        Integer.parseInt(currentLineElements(5)),
        Integer.parseInt(currentLineElements(6)),
        currentLineElements(7),
        currentLineElements(8).toDouble,
        currentLineElements(9).toDouble)
    }
    tweets.toList
  }
}
