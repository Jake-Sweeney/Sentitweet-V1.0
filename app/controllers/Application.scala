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
    "verified" -> boolean, "resultType" -> nonEmptyText, "maxNumResults" -> nonEmptyText, "randomGeolocations" -> boolean, "advancedUserQuery" -> nonEmptyText)
  (AdvancedSearch.apply)(AdvancedSearch.unapply))

  var userSearchQuery = ""
  var selectedFilter = ""
  var isVerified = ""
  var resultType = ""
  var maxNumberOfResults = ""
  var geolocations = ""

  def index() = listResults()

  def listResults() = Action {
    Ok(views.html.index(QueryController.getResults, searchForm, userSearchQuery, getTweetsAsJson(QueryController.getResults), getTweetSentimentCountAsJson(QueryController.getResults)))
  }

  def queryUserSearch() = Action { implicit request =>
    QueryController.clearResults
    QueryController.resetDefaultValues()
    println("User Search button clicked")
    searchForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(QueryController.getResults, errors, userSearchQuery, "", "")),
      userQuery => {
        userSearchQuery = userQuery
        QueryController.searchForTweets(userQuery)
        calculateSentimentOfTweets(QueryController.getResults)
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
    val tweetsAsJson = Json.stringify(Json.toJson(QueryController.getResults.toSeq))
    tweetsAsJson
  }

  def clearResults() = Action {
    QueryController.clearResults
    Redirect(routes.Application.index())
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
    filterForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(QueryController.getResults, errors, userSearchQuery, "", "")),
      sentiment => {
        println("Sentiment: " + sentiment)
        selectedFilter = sentiment
        if(sentiment == "positive") {
          filteredTweets = dataFilter.onlyPositiveTweets()
        } else if(sentiment == "negative") {
          filteredTweets = dataFilter.onlyNegativeTweets()
        } else if(sentiment == "neutral") {
          filteredTweets = dataFilter.onlyNeutralTweets()
        } else {
          filteredTweets = tweets
        }
      }
    )
    println("Selected Filter: " + selectedFilter)
    val filteredTweetsAsJson = getTweetsAsJson(filteredTweets)
    val filteredTweetsSentimentCount = getTweetSentimentCountAsJson(filteredTweets)
    Ok(views.html.index(filteredTweets, searchForm, userSearchQuery, filteredTweetsAsJson, filteredTweetsSentimentCount))
  }

  def queryAdvancedUserSearch() = Action { implicit request =>
    println("Advanced Search button clicked")
    QueryController.clearResults
    val advancedSearchData = advancedSearchForm.bindFromRequest
    advancedSearchData.fold(
      errors => BadRequest(views.html.index(QueryController.getResults, null, userSearchQuery, "", "")),
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

        userSearchQuery = validData.userSearchTerm
      }
    )

    println("randomGeo " + QueryController.randomGeolocatedTweets)
    println("verified " + QueryController.onlyVerifiedTweets)
    println("searchType " + QueryController.searchType)
    println("maxNumTweets " + QueryController.querySize)
    println("search term  " + userSearchQuery)

    QueryController.searchForTweets(userSearchQuery)
    calculateSentimentOfTweets(QueryController.getResults)
    Redirect(routes.Application.listResults())
  }

  def loadSampleData() = Action { implicit request =>
    val sampleDataLoader = new SampleDataLoader
    val tweets = sampleDataLoader.loadData()
    //wont need to calculate the sentiment in future, just for these particular sets of results.
    calculateSentimentOfTweets(tweets)
    Ok(views.html.index(tweets, searchForm, userSearchQuery, getTweetsAsJson(tweets), getTweetSentimentCountAsJson(tweets)))
  }
}
