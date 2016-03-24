package model

/**
  * Created by Jake on 01/03/2016.
  */
class DataFilter(tweets: List[Tweet]) {

  def onlyPositiveTweets(): List[Tweet] = {
    val positiveTweetsRDD = tweets.filter(tweet => tweet.sentiment == "POSITIVE")
    positiveTweetsRDD
  }

  def onlyNegativeTweets(): List[Tweet] = {
    val negativeTweetsRDD = tweets.filter(tweet => tweet.sentiment == "NEGATIVE")
    negativeTweetsRDD
  }

  def onlyNeutralTweets(): List[Tweet] = {
    val neutralTweetsRDD = tweets.filter(tweet => tweet.sentiment == "NEUTRAL")
    neutralTweetsRDD
  }

}
