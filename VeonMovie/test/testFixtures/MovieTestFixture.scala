package testFixtures

import model.{Movie, MovieDTO, MovieId}
import org.bson.types.ObjectId
import play.api.libs.json.Json

/**
  * Created by rametse on 2017/07/09.
  */
trait MovieTestFixture {


  val randomId = new ObjectId().toString

  val testMovieId = MovieId(randomId, "screen_123456")
  val testMovie = Movie(testMovieId, 100)
  val testMovieDTO = MovieDTO(testMovieId.imdbId, testMovieId.screenId,100)

  val testJsonMovie = Json.toJson(testMovie)
  val testJsonMovieId = Json.toJson(testMovieId)
  val testJsonMovieDTO = Json.toJson(testMovieDTO)


}
