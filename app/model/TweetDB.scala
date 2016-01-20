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

  val rowParser = SqlParser.long("status_id")

  def createTable(userQuery: String): Unit = {
    /**
      * create a table in the database for the most recent results.
      * use the search query and then the timestamp as a name.
      * e.g userQuery_timestamp
      */
  }

  def saveResults(tweets: List[Status]) = {
    for (tweet <- tweets) {
      val tweetid = tweet.getId.toString
      DB.withConnection { implicit connection =>
        try {
          val rowsChanged =
            SQL("INSERT INTO tweet VALUES(DEFAULT,{tweetid})")
              .on('tweetid -> tweetid)
              .executeUpdate()
          println(f"Inserted Tweet:$tweetid%s into table.")
        } catch {
          case e: Exception => e.printStackTrace()
        }
      }
    }
  }

  def getTweets(): List[Tweet] = DB.withConnection { implicit connection =>
    val statusIDs: List[Long] = SQL("SELECT status_id as id FROM tweet").as(rowParser *)
    val tweets: List[Tweet] = for (id <- statusIDs) yield new Tweet(id)
    tweets
  }
}
