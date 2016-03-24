package model

import java.io.InputStream

/**
  * Created by Jake on 24/03/2016.
  */
class SampleDataLoader {

  val tweets = scala.collection.mutable.MutableList[Tweet]()

  def loadData(): List[Tweet] = {
    val stream: InputStream = getClass.getResourceAsStream("/resources/sampleresults/SampleData_EasterWeekend.txt")
    val lines: Iterator[String] = scala.io.Source.fromInputStream(stream).getLines()
    for (line <- lines) {
      val currentLineElements = line.split(":;:")
          tweets += new Tweet(
            currentLineElements(0).toLong,
            currentLineElements(1),
            currentLineElements(2),
            currentLineElements(3),
            currentLineElements(4),
            Integer.parseInt(currentLineElements(5)),
            Integer.parseInt(currentLineElements(6))
          )
        }
      tweets.toList
    }
}
