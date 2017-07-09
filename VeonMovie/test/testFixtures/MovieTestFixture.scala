package testFixtures

import model.{Movie, MovieId}
import play.api.libs.json.Json

/**
  * Created by rametse on 2017/07/09.
  */
trait MovieTestFixture {

  val testmovieId = MovieId("tt0111161", "screen_123456")
  val testMovie = Movie(testmovieId, 100)

  val testJsonMovie = Json.toJson(testMovie)
  val testJsonMovieId = Json.toJson(testmovieId)

}
