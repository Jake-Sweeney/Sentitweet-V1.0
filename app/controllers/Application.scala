package controllers

import javax.inject.Inject

import model.TweetDB
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
    Ok(views.html.index(QueryController.getResults, searchForm))
  }

  def queryUserSearch() = Action { implicit request =>
    println("User Search button clicked")
    searchForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(QueryController.getResults, errors)),
      userQuery => {
        userSearchQuery = userQuery
        QueryController.searchForTweets(userQuery)
        Redirect(routes.Application.listResults())
      }
    )
  }

  def saveSearchResults() = Action { implicit request =>
    //TODO call create table using the query and curdate.
    println(f"In the saveSearchResults method. ${QueryController.getResults.size}%d")
    TweetDB.saveResults(QueryController.getResults)
    Redirect(routes.Application.listResults())
  }

  def graphButtonClicked() = Action { implicit request =>
    println("Graph button clicked")
    /*List of tweets are first converted to Seq type, then converted to a JsValue
      which is then converted to a string containing Json syntax.
     */
    val tweets = Json.stringify(Json.toJson(QueryController.getResults.toSeq))
    println(tweets)
    Ok(views.html.graphs(userSearchQuery, tweets))
  }

}
