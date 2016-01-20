package model

import controllers.TwitterInstance
import twitter4j.Status

/**
  * Created by Jake on 17/01/2016.
  */
class Tweet(tweetId: Long) extends TwitterInstance {

  val status: Status = twitter.showStatus(tweetId)

  def getText: String = {
    val tweetText = status.getText
    tweetText
  }

  def getUserName: String = {
    val tweetUserName = status.getUser.getScreenName
    tweetUserName
  }

  def getTimeCreated: String = {
    val timeCreated = status.getCreatedAt.toString
    timeCreated
  }
}
