package domain

import play.api.libs.json._

case class Movie(title: String, year: String, plot: String, imdbID: String, genre: String = "")

object Movie {
  implicit val movieFormat: Format[Movie] = Json.format[Movie]
}
