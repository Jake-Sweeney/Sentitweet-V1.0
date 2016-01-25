package controllers

import javax.inject.Inject

import model.TweetDB
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

class Application @Inject() (val messagesApi : MessagesApi) extends Controller with I18nSupport {

  var userSearchQuery = ""
  val searchForm = Form("userQuery" -> nonEmptyText)

  def index() = listResults

  def listResults() = Action {
    Ok(views.html.index(QueryController.searchResults, searchForm))
  }

  def queryUserSearch() = Action { implicit request =>
    searchForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(QueryController.searchResults, errors)),
      userQuery => {
        userSearchQuery = userQuery
        QueryController.searchForTweets(userQuery)
        Redirect(routes.Application.listResults)
      }
    )
  }

  def saveSearchResults() = Action { implicit request =>
    //call create table using the query and curdate.
    println(f"In the saveSearchResults method. ${QueryController.searchResults.size}%d")
    TweetDB.saveResults(QueryController.searchResults)
    Redirect(routes.Application.listResults())
  }
}