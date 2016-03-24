package model


import play.api.libs.json._

/**
  * Created by Jake on 17/01/2016.
  */

case class Tweet(id: Long, username: String, profileImageUrl: String, date: String, text: String, favouriteCount: Int, retweetCount: Int, var sentiment: String  = "", var longitude: Double = 0.0, var latitude: Double = 0.0)

object Tweet {

  implicit object TweetFormat extends Format[Tweet] {

    //When Json.toJson() is called this structure is used for objects of Tweet type.
    def writes(tweet: Tweet): JsValue = {
      val tweetSeq = Seq(
        "id" -> JsNumber(tweet.id),
        "username" -> JsString(tweet.username),
        "profileImageURL" -> JsString(tweet.profileImageUrl),
        "date" -> JsString(tweet.date),
        "text" -> JsString(tweet.text),
        "favouriteCount" -> JsNumber(tweet.favouriteCount),
        "retweetCount" -> JsNumber(tweet.retweetCount),
        "sentiment" -> JsString(tweet.sentiment),
        "tweetGeolocation" -> JsObject(Seq(
          "lng" -> JsNumber(tweet.longitude),
          "lat" -> JsNumber(tweet.latitude)
        ))
      )
      JsObject(tweetSeq)
    }

    //This reads method is effectively a null op method as it is only here to satisfy Format being extended.
    def reads(json: JsValue): JsResult[Tweet] = {
      JsSuccess(Tweet(0L,"", "", "", "", 0, 0, "", 0, 0))
    }
  }
}
