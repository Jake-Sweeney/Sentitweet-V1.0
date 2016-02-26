package model


import play.api.libs.json._

/**
  * Created by Jake on 17/01/2016.
  */

case class Tweet(id: Long, username: String, date: String, text: String, favouriteCount: Int, retweetCount: Int, var sentiment: String  = "")

object Tweet {

  var sentiement = ""

  implicit object TweetFormat extends Format[Tweet] {

    //When Json.toJson() is called this structure is used for objects of Tweet type.
    def writes(tweet: Tweet): JsValue = {
      val tweetSeq = Seq(
        "id" -> JsNumber(tweet.id),
        "username" -> JsString(tweet.username),
        "date" -> JsString(tweet.date),
        "text" -> JsString(tweet.text),
        "favouriteCount" -> JsNumber(tweet.favouriteCount),
        "retweetCount" -> JsNumber(tweet.retweetCount),
        "sentiment" -> JsString(tweet.sentiment)
      )
      JsObject(tweetSeq)
    }

    //This reads method is effectively a null op method as it is only here to satisfy Format being extended.
    def reads(json: JsValue): JsResult[Tweet] = {
      JsSuccess(Tweet(0L,"", "", "", 0, 0))
    }
  }

  /*Can't access these from the object reference i.e. loops. */

  def sentiment_(value: String) = sentiement = value

  def sentiment: String = sentiement

}
