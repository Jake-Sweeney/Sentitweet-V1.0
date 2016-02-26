package controllers

import model.Tweet

import twitter4j.Query
import twitter4j.Query.ResultType

import scala.util.control.Breaks._
import scala.collection.JavaConversions._

/**
  * Created by Jake on 19/01/2016.
  */
object QueryController extends TwitterInstance {

  var querySize = 10
  var batchSearch: Boolean = false
  val maxQueries = 5
  private val tweets = scala.collection.mutable.MutableList[Tweet]()

  def searchForTweets(userQuery: String): List[Tweet] = {
    var totalTweets = 0
    var maxID: Long = -1

    try {
      var tweetSearchRateLimit = twitter.getRateLimitStatus("search").get("/search/tweets")
      //TODO replace this with logging.
      printf("There are %d calls remaining out of %d.\nLimit resets in %d seconds.\n",
        tweetSearchRateLimit.getRemaining, tweetSearchRateLimit.getLimit, tweetSearchRateLimit.getSecondsUntilReset)

      breakable {
        for (i <- 0 until maxQueries) {
          printf("Iteration: %d\n", i)

          if (tweetSearchRateLimit.getRemaining == 0) {
            printf("Application must wait for %d seconds due to rate limits.\n", tweetSearchRateLimit.getSecondsUntilReset)
            Thread.sleep((tweetSearchRateLimit.getSecondsUntilReset + 2) * 10001)
          }

          val query = new Query(userQuery)
          query.setCount(querySize)
          query.resultType(ResultType.recent)
          query.setLang("en")

          if (maxID != -1) query.setMaxId(maxID - 1)

          val queryResult = twitter.search(query)

          if (queryResult.getTweets.size() == 0) break

          //Cleaing the text of tweet won't be done here, it will be moved to a new class and called when sentiment analysis occurs.
          for(status <- queryResult.getTweets) {
            totalTweets = totalTweets + 1
            if (maxID == -1 || status.getId < maxID) maxID = status.getId
            tweets += new Tweet(
              status.getId,
              status.getUser.getScreenName,
              status.getCreatedAt.toString,
              status.getText,
              status.getFavoriteCount,
              status.getRetweetCount)
          }
          tweetSearchRateLimit = queryResult.getRateLimitStatus
        }
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
    printf("A total of %d tweets were gathered.\n", totalTweets)
    tweets.toList
  }

  //The batchSearch flag is for larger queries later.
  //Possibly have radio buttons instead to have more control over the amounts selected.
  //e.g. search amount = O 30 | O 50 | O 100 | O 150
  def querySize_(size: Int) = {
    if (size > 100) {
      batchSearch = true
      querySize = size
    } else querySize = size
  }

  def getResults: List[Tweet] = tweets.toList

  def clearResults = tweets.clear
}
