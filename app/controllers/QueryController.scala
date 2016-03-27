package controllers

import model.Tweet

import twitter4j.{Status, Query}
import twitter4j.Query.ResultType

import scala.util.control.Breaks._
import scala.collection.JavaConversions._

/**
  * Created by Jake on 19/01/2016.
  */
object QueryController extends TwitterInstance {

  var querySize = 100
  var numberOfQueries = 1
  var onlyVerifiedTweets = false
  var randomGeolocatedTweets = false
  var searchType = "recent"
  private val tweets = scala.collection.mutable.MutableList[Tweet]()

  def searchForTweets(userQuery: String): List[Tweet] = {
    var totalTweets = 0
    var maxID: Long = -1

    try {
      var tweetSearchRateLimit = twitter.getRateLimitStatus("search").get("/search/tweets")
      printf("There are %d calls remaining out of %d.\nLimit resets in %d seconds.\n",
        tweetSearchRateLimit.getRemaining,
        tweetSearchRateLimit.getLimit,
        tweetSearchRateLimit.getSecondsUntilReset)

      breakable {
        for (i <- 0 until numberOfQueries) {
          printf("Iteration: %d\n", i)

          if (tweetSearchRateLimit.getRemaining == 0) {
            printf("Application must wait for %d seconds due to rate limits.\n", tweetSearchRateLimit.getSecondsUntilReset)
            Thread.sleep((tweetSearchRateLimit.getSecondsUntilReset + 2) * 10001)
          }

          val query = createSearchQuery(userQuery)

          if (maxID != -1) query.setMaxId(maxID - 1)

          val queryResult = twitter.search(query)

          if (queryResult.getTweets.size() == 0) break

          if(onlyVerifiedTweets) {
            for(status <- queryResult.getTweets) {
              if (maxID == -1 || status.getId < maxID) maxID = status.getId
              if(status.getUser.isVerified) {
                addTweet(status)
                totalTweets = totalTweets + 1
              }
            }
          } else {
            for(status <- queryResult.getTweets) {
              if (maxID == -1 || status.getId < maxID) maxID = status.getId
              addTweet(status)
              totalTweets = totalTweets + 1
            }
          }
          tweetSearchRateLimit = queryResult.getRateLimitStatus
        }
      }
      printf("There are %d calls remaining out of %d.\nLimit resets in %d seconds.\n",
              tweetSearchRateLimit.getRemaining,
              tweetSearchRateLimit.getLimit,
              tweetSearchRateLimit.getSecondsUntilReset)
    } catch {
      case e: Exception => e.printStackTrace()
    }
    printf("A total of %d tweets were gathered.\n", totalTweets)
    tweets.toList
  }

  def addTweet(status: Status) = {
    if(randomGeolocatedTweets) {
      println("RANDOM GEO")
      val (longitude, latitude) = generateARandomGeolocation
      println((longitude, latitude))
      tweets += new Tweet(
        status.getId,
        status.getUser.getScreenName,
        status.getUser.getBiggerProfileImageURLHttps,
        status.getCreatedAt.toString,
        status.getText,
        status.getFavoriteCount,
        status.getRetweetCount,
        "",
        longitude,
        latitude
      )
    } else {
      if(status.getGeoLocation == null) {
        tweets += new Tweet(
          status.getId,
          status.getUser.getScreenName,
          status.getUser.getBiggerProfileImageURLHttps,
          status.getCreatedAt.toString,
          status.getText,
          status.getFavoriteCount,
          status.getRetweetCount
        )
//        println(status.getId + ":;:" +
//          status.getUser.getScreenName + ":;:" +
//          status.getUser.getBiggerProfileImageURLHttps + ":;:" +
//          status.getCreatedAt.toString + ":;:" +
//          status.getText + ":;:" +
//          status.getFavoriteCount + ":;:" +
//          status.getRetweetCount)
      } else {
        tweets += new Tweet(
          status.getId,
          status.getUser.getScreenName,
          status.getUser.getBiggerProfileImageURLHttps,
          status.getCreatedAt.toString,
          status.getText,
          status.getFavoriteCount,
          status.getRetweetCount,
          "",
          status.getGeoLocation.getLongitude,
          status.getGeoLocation.getLatitude
        )
//        println(status.getId + ":;:" +
//          status.getUser.getScreenName + ":;:" +
//          status.getUser.getBiggerProfileImageURLHttps + ":;:" +
//          status.getCreatedAt.toString + ":;:" +
//          status.getText + ":;:" +
//          status.getFavoriteCount + ":;:" +
//          status.getRetweetCount + ":;:" +
//          "" + ":;:" +
//          status.getGeoLocation.getLongitude + ":;:" +
//          status.getGeoLocation.getLatitude)
      }
    }
  }

  def getResults: List[Tweet] = tweets.toList

  def setResults(sampleTweets: List[Tweet]) = {
    for(status <- sampleTweets) {
      tweets += new Tweet(
          status.id,
          status.username,
          status.profileImageUrl,
          status.date,
          status.text,
          status.favouriteCount,
          status.retweetCount,
          status.sentiment,
          status.longitude,
          status.latitude)
    }
  }

  def clearResults = tweets.clear

  def setInitialResultsSize(size: Int) = {
    if(size < 100) {
      querySize = size
      numberOfQueries = 1
    } else {
      numberOfQueries = size/100
      querySize = 100
    }
  }

  def setOnlyVerifiedTweets(onlyVerified : Boolean) = onlyVerifiedTweets = onlyVerified

  def setRandomGeolocatedTweets(isRandomlyGeolocated: Boolean) = randomGeolocatedTweets = isRandomlyGeolocated

  def setSearchType(searchType: String) = this.searchType = searchType

  def resetDefaultValues(): Unit = {
    querySize = 100
    numberOfQueries = 1
    onlyVerifiedTweets = false
    randomGeolocatedTweets = false
    searchType = "recent"
  }

  def createSearchQuery(userSearchQuery: String): Query = {
    val query = new Query(userSearchQuery)
    query.setCount(querySize)
    if(searchType == "recent") {
      query.resultType(ResultType.recent)
    } else if(searchType == "popular") {
      query.resultType(ResultType.popular)
    } else if(searchType == "mixed") {
      query.resultType(ResultType.mixed)
    }
    query.setLang("en")
    query
  }

  def generateARandomGeolocation: (Double, Double) = {
    val random = scala.util.Random
    val latitude = random.nextInt(180)-90.00
    val longitude = random.nextInt(360)-180.00
    (latitude, longitude)
  }
}
