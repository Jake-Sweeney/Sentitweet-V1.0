package controllers

import javax.inject.Inject

import model.TweetDB
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

class Application @Inject() (val messagesApi : MessagesApi) extends Controller with I18nSupport {

  val searchForm = Form("userQuery" -> nonEmptyText)

  def index() = listResults

  def listResults() = Action {
    Ok(views.html.index(TweetDB.getTweets(), searchForm))
  }

  def queryUserSearch() = Action { implicit request =>
    searchForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(TweetDB.getTweets(), errors)),
      userQuery => {
        val tweets = QueryController.searchForTweets(userQuery)
        TweetDB.saveResults(tweets)
        Redirect(routes.Application.listResults)
      }
    )
  }


}
