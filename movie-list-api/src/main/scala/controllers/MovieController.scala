package controllers

import client.MovieProviderClient
import domain.{Movie, UserPreference}
import play.api.mvc._
import play.api.libs.json._
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class MovieController @Inject()(cc: ControllerComponents, movieClient: MovieProviderClient)
                               (implicit ec: ExecutionContext) extends AbstractController(cc) {
  
  private var preferences = List.empty[UserPreference]
  
  def getMovie(title: String) = Action.async {
    movieClient.getMovie(title).map {
      case Some(movie) => Ok(Json.toJson(movie))
      case None => NotFound("Movie not found")
    }
  }
  
  def addPreference() = Action.async(parse.json) { request =>
    request.body.validate[UserPreference] match {
      case JsSuccess(pref, _) =>
        movieClient.addPreference(pref).map { success =>
          if (success) {
            preferences = pref :: preferences
            Created(Json.toJson(pref))
          } else {
            InternalServerError("Failed to store preference")
          }
        }
      case JsError(_) =>
        scala.concurrent.Future.successful(BadRequest("Invalid preference data"))
    }
  }
  
  def getUserPreferences(userId: String) = Action.async {
    movieClient.getUserPreferences(userId).map { prefs =>
      Ok(Json.toJson(prefs))
    }
  }

  def getMovieSuggestion(userId: String) = Action.async {
    movieClient.getMovieSuggestion(userId).map {
      case Some(movie) => Ok(Json.toJson(movie))
      case None => NotFound("No suggestion available")
    }
  }
}
