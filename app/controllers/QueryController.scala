package controllers

import twitter4j.{Query, Status}

import scala.collection.JavaConversions._

/**
  * Created by Jake on 19/01/2016.
  */
object QueryController extends TwitterInstance {

  var querySize = 40
  var batchSearch: Boolean = false

  def searchForTweets(userQuery: String) = {
    val query = new Query(userQuery)
    query.setCount(querySize)
    val searchResults: List[Status] = twitter.search(query).getTweets.toList
    searchResults
  }

  //The batchSearch flag is for larger queries later.
  //Possibly have radio buttons instead to have more control over the amounts selected.
  //e.g. seach amount = O 30 | O 50 | O 100 | O 150
  def querySize_(size: Int) = {
    if (size > 100) {
      batchSearch = true
      querySize = size
    } else querySize = size
  }
}
