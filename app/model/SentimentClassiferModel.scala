package model

import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import model.Sentiment.Sentiment
import play.api.libs.json.{JsString, JsObject, Json}

import scala.collection.convert.wrapAll._
import scala.collection.immutable.HashMap

/**
  * Created by Jake on 18/02/2016.
  */
object SentimentClassiferModel {

  val properties = new Properties
  properties.setProperty("annotators", "tokenize, ssplit, parse, sentiment")

  val pipeline: StanfordCoreNLP = new StanfordCoreNLP(properties)

  def classifyTweetSentiment(tweets: List[Tweet]): List[Tweet] = {
    val tweetsWithSentiment = for(tweet <- tweets) yield {
      val sentiment = mainSentiment(TweetCleaner.cleanText(tweet.text))
      tweet.sentiment = sentiment.toString
      tweet
    }
    tweetsWithSentiment
  }
  def mainSentiment(text: String): Sentiment = Option(text) match {
    case Some(text) if !text.isEmpty => extractSentiment(text)
    case _ => throw new IllegalArgumentException("Input is invalid")
  }

  private def extractSentiment(text: String): Sentiment = {
    val (_, sentiment) = extractSentiments(text)
      .maxBy {
        case (sentence,_) => sentence.length
      }
    sentiment
  }

  def extractSentiments(text: String): List[(String, Sentiment)] = {
    val annotation: Annotation = pipeline.process(text)
    val sentences = annotation.get(classOf[CoreAnnotations.SentencesAnnotation])
    sentences
      .map(sentence => (sentence, sentence.get(classOf[SentimentCoreAnnotations.SentimentAnnotatedTree])))
      .map {
        case (sentence, tree) => (sentence.toString, Sentiment.toSentiment(RNNCoreAnnotations.getPredictedClass(tree)))
      }
      .toList
  }

  def countSentiments(tweets: List[Tweet]): String = {
    var positiveCount = 0
    var negativeCount = 0
    var neutralCount = 0
    for(tweet <- tweets) {
      println(tweet.sentiment)
      tweet.sentiment match {
        case "POSITIVE" => positiveCount += 1
        case "NEGATIVE" => negativeCount += 1
        case "NEUTRAL" => neutralCount += 1
        case _ => println("No match found")
      }
    }
    val sentimentTotalsAsJson = Json.arr(
      Json.obj(
        "sentiment" -> "positive",
        "value" -> positiveCount
      ),
      Json.obj(
        "sentiment" -> "negative",
        "value" -> negativeCount
      ),
      Json.obj(
        "sentiment" -> "neutral",
        "value" -> neutralCount
      )
    )
    Json.stringify(sentimentTotalsAsJson)
  }
}
