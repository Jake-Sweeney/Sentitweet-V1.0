package model

/**
  * Created by Jake on 18/02/2016.
  */
object Sentiment extends Enumeration {

  type Sentiment = Value

  val POSITIVE, NEGATIVE, NEUTRAL = Value

  def toSentiment(sentiement: Int): Sentiment = sentiement match {
    case x if x == 0 || x == 1 => Sentiment.NEGATIVE
    case 2 => Sentiment.NEUTRAL
    case x if x == 3 || x == 4 => Sentiment.POSITIVE
  }

  override def toString() = Sentiment.toString
}
