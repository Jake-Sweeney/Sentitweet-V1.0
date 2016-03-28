package controllers

import javax.inject.Inject

import model._
import org.apache.spark.{SparkContext, SparkConf}
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._

class Application @Inject() (val messagesApi : MessagesApi) extends Controller with I18nSupport {

  val searchForm = Form("userQuery" -> nonEmptyText)
  val filterForm = Form("sentiment" -> nonEmptyText)
  val advancedSearchForm = Form(mapping(
    "verified" -> boolean, "resultType" -> nonEmptyText, "maxNumResults" -> nonEmptyText, "randomGeolocations" -> boolean)
  (AdvancedSearchSettings.apply)(AdvancedSearchSettings.unapply))
  val sampleDataForm = Form("sampleDataFileName" -> nonEmptyText)

  var userSearchQuery = ""
  var isVerified = ""
  var resultType = ""
  var maxNumberOfResults = ""
  var geolocations = ""
  var selectedFilter = "none"

  def index() = listResults()

  def listResults() = Action {
    Ok(views.html.index(QueryController.getResults, searchForm, userSearchQuery, getTweetsAsJson(QueryController.getResults), getTweetSentimentCountAsJson(QueryController.getResults), selectedFilter))
  }

  def queryUserSearch() = Action { implicit request =>
    QueryController.clearResults
    selectedFilter = "none"
    println("User Search button clicked")
    searchForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(QueryController.getResults, errors, userSearchQuery, "", "", selectedFilter)),
      userQuery => {
        userSearchQuery = userQuery
        println("SEARCH SETTINGS")
        println("randomGeo " + QueryController.randomGeolocatedTweets)
        println("verified " + QueryController.onlyVerifiedTweets)
        println("searchType " + QueryController.searchType)
        println("maxNumTweets " + QueryController.querySize)
        println("search term  " + userSearchQuery)
        QueryController.searchForTweets(userQuery)
        if(QueryController.getResults.nonEmpty) {
          calculateSentimentOfTweets(QueryController.getResults)
          for(status <- QueryController.getResults) {
            println(status.id + ":;:" +
              status.username + ":;:" +
              status.profileImageUrl + ":;:" +
              status.date + ":;:" +
              status.text + ":;:" +
              status.favouriteCount + ":;:" +
              status.retweetCount + ":;:" +
              status.sentiment + ":;:" +
              status.longitude + ":;:" +
              status.latitude)
          }
          Redirect(routes.Application.listResults())
        } else
          Redirect(routes.Application.listResults())
      }
    )
  }

  def calculateSentimentOfTweets (tweets: List[Tweet]): Unit = {
    val tweetsWithSentiment: List[Tweet] = SentimentClassiferModel.classifyTweetSentiment(tweets)
  }

  def getTweetSentimentCountAsJson(tweets: List[Tweet]): String = {
    val tweetsWithSentimentCount = SentimentClassiferModel.countSentiments(tweets)
    tweetsWithSentimentCount
  }

  def getTweetsAsJson(tweets: List[Tweet]): String = {
    val tweetsAsJson = Json.stringify(Json.toJson(tweets.toSeq))
    tweetsAsJson
  }

  def saveSearchResults() = Action { implicit request =>
    //TODO call create table using the query and curdate.
    println(f"In the saveSearchResults method. ${QueryController.getResults.size}%d")
    TweetDB.saveResults(QueryController.getResults)
    Redirect(routes.Application.listResults())
  }


  def filterResults() = Action { implicit request =>
    println("Filter results called")
    val tweets = QueryController.getResults
    val dataFilter = new DataFilter(tweets)
    var filteredTweets = List[Tweet]()
    selectedFilter = "none"
    filterForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(QueryController.getResults, errors, userSearchQuery, "", "", selectedFilter)),
      sentiment => {
        println("Sentiment: " + sentiment)
        selectedFilter = sentiment
        if(sentiment == "positive") {
          filteredTweets = dataFilter.onlyPositiveTweets()
          selectedFilter = "positive"
        } else if(sentiment == "negative") {
          filteredTweets = dataFilter.onlyNegativeTweets()
          selectedFilter = "negative"
        } else if(sentiment == "neutral") {
          filteredTweets = dataFilter.onlyNeutralTweets()
          selectedFilter = "neutral"
        } else {
          filteredTweets = tweets
          selectedFilter = "none"
        }
      }
    )
    println("Selected Filter: " + selectedFilter)
    val filteredTweetsAsJson = getTweetsAsJson(QueryController.getResults)
    val filteredTweetsSentimentCount = getTweetSentimentCountAsJson(QueryController.getResults)
    Ok(views.html.index(filteredTweets, searchForm, userSearchQuery, filteredTweetsAsJson, filteredTweetsSentimentCount, selectedFilter))
  }

  def saveAdvancedSearchSettings() = Action { implicit request =>
    println("Advanced Search Settings button clicked")
    selectedFilter = "none"
    val advancedSearchData = advancedSearchForm.bindFromRequest
    advancedSearchData.fold(
      errors => BadRequest(views.html.index(QueryController.getResults, null, userSearchQuery, "", "",selectedFilter)),
      validData => {
        if (!validData.verified) {
          QueryController.setOnlyVerifiedTweets(false)
        } else {
          QueryController.setOnlyVerifiedTweets(true)
        }

        if (validData.resultType == "recent") {
          QueryController.setSearchType("recent")
        } else if (validData.resultType == "popular") {
          QueryController.setSearchType("popular")
        } else if (validData.resultType == "mixed") {
          QueryController.setSearchType("mixed")
        }

        val maxNumTweets = Integer.parseInt(validData.maxNumberOfTweets)
        QueryController.setInitialResultsSize(maxNumTweets)

        if (!validData.randomGeolocations) {
          QueryController.setRandomGeolocatedTweets(false)
        } else if (validData.randomGeolocations) {
          QueryController.setRandomGeolocatedTweets(true)
        }
      }
    )

    println("randomGeo " + QueryController.randomGeolocatedTweets)
    println("verified " + QueryController.onlyVerifiedTweets)
    println("searchType " + QueryController.searchType)
    println("maxNumTweets " + QueryController.querySize)
    println("search term  " + userSearchQuery)
    Redirect(routes.Application.listResults())
  }

  def loadSampleData() = Action { implicit request =>
    sampleDataForm.bindFromRequest.fold(
      errors =>   BadRequest(views.html.index(QueryController.getResults, errors, userSearchQuery, "", "", selectedFilter)),
      sampleDataFileName => {
        val sampleDataLoader = new SampleDataLoader(sampleDataFileName)
        val tweets: List[Tweet] = sampleDataLoader.loadData()
        QueryController.clearResults
        QueryController.setResults(tweets)
        var searchValue = sampleDataFileName.replace(".txt", "")
        searchValue = searchValue.replace("SampleData_", "")
        Ok(views.html.index(tweets, searchForm, searchValue, getTweetsAsJson(tweets), getTweetSentimentCountAsJson(tweets), selectedFilter))
      }
    )
  }

  def loadAboutPage() = Action {
    Ok(views.html.about())
  }

  def resetSearchSettings() =  Action { implicit request =>
    QueryController.resetDefaultValues()
    Redirect(routes.Application.listResults())
  }
}
