package model

/**
  * Created by Jake on 20/01/2016.
  */

import anorm._
import anorm.SQL
import anorm.SqlParser._

import play.api.Play.current
import play.api.db.DB
import twitter4j.Status

object TweetDB {

  val tweetidParser = SqlParser.long("status_id")

  //explicitly state object type for Parsers.
  val tweetSetParser: ResultSetParser[List[(String,String,String,String)]] = {
    get[String]("status_id")~get[String]("status_user")~get[String]("status_time")~get[String]("status_text") map(flatten) *
  }

  def createTable(userQuery: String): Unit = {
    /**
      * create a table in the database for the most recent results.
      * use the search query and then the timestamp as a name.
      * e.g userQuery_timestamp
      */
  }

  def saveResults(tweets: List[Status]) = {
    for (tweet <- tweets) {
      val tweetID = tweet.getId.toString
      val tweetUserName = tweet.getUser.getScreenName
      val tweetTime = tweet.getCreatedAt.toString
      val tweetText = tweet.getText
      println(f"$tweetID, $tweetUserName, $tweetTime, $tweetText")
      DB.withConnection { implicit connection =>
        try {
          val rowsChanged =
            SQL("INSERT INTO tweet VALUES (DEFAULT,{tweetID},{tweetUserName},{tweetTime},{tweetText})")
              .on('tweetID -> tweetID, 'tweetUserName -> tweetUserName,
                'tweetTime -> tweetTime, 'tweetText -> tweetText)
              .executeUpdate()
          println(f"Inserted Tweet:$tweetID%s into table.")
        } catch {
          case e: Exception => e.printStackTrace()
        }
      }
    }
    DB.withConnection { implicit connection =>
      val savedResults =
        SQL("SELECT count(id) as id FROM tweet").apply.head
      val resultsCount = savedResults[Int]("id")
      println(f"$resultsCount%d results saved.")
    }
  }

  def getSampleTweets(): List[TweetSet] = DB.withConnection { implicit connection =>
    val statuses: List[(String,String,String,String)] = {
      SQL("SELECT status_id as id, status_user as user, " +
        "status_time as time, status_text as text FROM tweet").as(tweetSetParser)
    }
    val tweets: List[TweetSet] = for ((id,user,time,text) <- statuses) yield new TweetSet(id.toLong,user,time,text)
    tweets
  }
}
