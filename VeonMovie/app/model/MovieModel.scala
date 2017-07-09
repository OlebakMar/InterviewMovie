package model

import com.novus.salat.dao.SalatDAO
import com.novus.salat.grater
import play.api.libs.json.Json
import scala.language.implicitConversions
import com.mongodb.casbah.Imports._
import model.mongoContext._

/**
  * This file holds all the class/objects
  * that define a Movie or can be used as helpers in
  * processing a movie.
  *
  * A movie has the following JSON structure
  *
  * {
      "imdbId": "tt0111161",
      "screenId": "screen_123456",
      "movieTitle": "The Shawshank Redemption",
      "availableSeats": 100,
      "reservedSeats": 50
    }
  *This file currently does not cater for the field reservedSeats
  * as it is unclear how the field is set
  * Created by rametse on 2017/07/05.
  */

case class MovieId(imdbId: String, screenId: String)

object MovieId {
  implicit val movieIdReads = Json.reads[MovieId]
  implicit val movieIdWrites = Json.writes[MovieId]
}

case class Movie(_id: MovieId,
                 //movieTitle: String,
                 availableSeats: Int,
                 reservedSeats: Int = 0){
  def hasAvailableSeats = reservedSeats < availableSeats
}

object Movie {
  implicit val movieReads = Json.reads[Movie]
  implicit val movieWrites = Json.writes[Movie]
}

object MovieDAO extends SalatDAO[Movie, MovieId](collection= MongoConnection()("interview")("movie"))

case class MovieQueryParams(_id: Option[MovieId] = None,
                                MovieId: Option[String] = None,
                                availableSeats: Option[Int] = None,
                                reservedSeats: Option[Int] = None){
  def toDBObject : DBObject =  grater[MovieQueryParams].asDBObject(this)
}

case class MovieDTO(imdbId: String,
                    screenId: String,
                    //  movieTitle: String,
                    availableSeats: Int,
                    reservedSeats: Option[Int] = None){
  def toMovie = Movie(MovieId(imdbId,screenId),availableSeats)
}

object MovieDTO {
  implicit val movieDTOReads = Json.reads[MovieDTO]
  implicit val movieDTOWrites = Json.writes[MovieDTO]
}

case class MovieInput(imdbId: String,
                      screenId: String,
                      movieTitle: String,
                      availableSeats: Int,
                      reservedSeats: Int=0)


case class RegisterMovie(movie: Movie)
case class ReserveSeatAtMovie(movieId: MovieId)
case class RetrieveMovie(movieId: MovieId)
case class SaveMovie(movie: Movie)
case class UpdateMovie(movie: Movie)
