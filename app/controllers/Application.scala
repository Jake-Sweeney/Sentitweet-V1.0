package controllers

import javax.inject.Inject

import model.{Tweet, SentimentClassiferModel, TweetDB}
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._

class Application @Inject() (val messagesApi : MessagesApi) extends Controller with I18nSupport {

  var userSearchQuery = ""
  val searchForm = Form("userQuery" -> nonEmptyText)

  def index() = listResults()

  def listResults() = Action {
    Ok(views.html.index(QueryController.getResults, searchForm, getTweetsAsJson(QueryController.getResults), getTweetSentimentCountAsJson(QueryController.getResults)))
  }

  def queryUserSearch() = Action { implicit request =>
    QueryController.clearResults
    println("User Search button clicked")
    searchForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(QueryController.getResults, errors, "", "")),
      userQuery => {
        userSearchQuery = userQuery
        QueryController.searchForTweets(userQuery)
        calculateSentimentOfTweets(QueryController.getResults)
        Redirect(routes.Application.listResults())
      }
    )
  }

  def graphButtonClicked() = Action { implicit request =>
    println("Graph button clicked")
    /*List of tweets are first converted to Seq type, then converted to a JsValue
      which is then converted to a string containing Json syntax.
     */
    val tweetsWithSentiment: List[Tweet] = SentimentClassiferModel.classifyTweetSentiment(QueryController.getResults)
    val tweetSentimentCount: String = SentimentClassiferModel.countSentiments(tweetsWithSentiment)
    val tweetsAsJson = Json.stringify(Json.toJson(tweetsWithSentiment.toSeq))
    println(tweetSentimentCount)
    println(tweetsAsJson)
    Ok(views.html.graphs(userSearchQuery, QueryController.getResults, tweetsAsJson, tweetSentimentCount))
  }

  def calculateSentimentOfTweets (tweets: List[Tweet]): Unit = {
    val tweetsWithSentiment: List[Tweet] = SentimentClassiferModel.classifyTweetSentiment(QueryController.getResults)
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


//  def filterResults(): Unit ={
//
//  }

  /*def getTweetSentiment() = Action { implicit request =>
    val tweetsWithSentiment = SentimentClassiferModel.classifyTweetSentiment(QueryController.getResults)
    for(tweet <- tweetsWithSentiment) {
      printf("Text: %s\t\t->Sentiment %s\n", tweet.text, tweet.sentiment)
    }
    Ok(views.html.index(QueryController.getResults, searchForm))
  }*/

}
