package domain

import play.api.libs.json._

case class UserPreference(userId: String, movieId: String, rating: Int)

object UserPreference {
  implicit val preferenceFormat: Format[UserPreference] = Json.format[UserPreference]
}
