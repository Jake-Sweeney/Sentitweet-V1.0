package controllers

import model.Tweet
import twitter4j.Query

import scala.collection.JavaConversions._

/**
  * Created by Jake on 19/01/2016.
  */
object QueryController extends TwitterInstance {

  var querySize = 15
  var batchSearch: Boolean = false
  private var tweets = List[Tweet]()

  def searchForTweets(userQuery: String): List[Tweet] = {
    val query = new Query(userQuery)
    query.setCount(querySize)
    val searchResults = twitter.search(query).getTweets.toList

    println(f"Query returned ${searchResults.size}%d results.")

    tweets = for (status <- searchResults) yield new Tweet(
      status.getId, status.getUser.getScreenName, status.getCreatedAt.toString, status.getText,
      status.getFavoriteCount, status.getRetweetCount
    )
    tweets
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

  def getResults: List[Tweet] = tweets
}
