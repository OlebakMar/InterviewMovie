package model

/**
  * Created by rametse on 2017/07/05.
  */

import org.scalatest.{BeforeAndAfterAll, _}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json._
import play.api.{Application, Play}
import testFixtures.MovieTestFixture
class MovieModelSpec extends FlatSpec
  with Matchers
  with BeforeAndAfterAll
  with MovieTestFixture{

  val application: Application = new GuiceApplicationBuilder().build()


  override def beforeAll(): Unit = {
    //use fake application instead
    Play.start(application)
  }

  override def afterAll(): Unit = {
    Play.stop(application)
  }

  "MovieDAO" should "perform CRUD functions on database" in{

    val updatedMovie = testMovie.copy(reservedSeats = 22)

    // Create:
    val id = MovieDAO.insert(testMovie) //save does upsert
    //id.isDefined should be(true) this is always empty due to lack of Salat support for compisite keys

    //Update
    val updateResult = MovieDAO.update(MovieQueryParams(_id = Some(testMovieId)).toDBObject,MovieDAO.toDBObject(updatedMovie))
    updateResult.isUpdateOfExisting should be(true)

    // Read:
    val getResult = MovieDAO.find(MovieQueryParams(_id = Some(testMovieId)).toDBObject).toList.headOption
    getResult.isDefined should be(true)
    getResult should equal(Some(updatedMovie))

    //Delete
    MovieDAO.remove(MovieQueryParams(_id = Some(testMovieId)).toDBObject)
    val removedResult = MovieDAO.find(MovieQueryParams(_id = Some(testMovieId)).toDBObject).toList.headOption
    removedResult.isDefined should be(false)

  }


  "MovieDTO " should "deserialized from JSON correctly " in {

    val createMovieRequestJSON = Json.parse(
      """
        |{
        |  "imdbId": "tt0111161",
        |  "screenId": "screen_123456",
        |  "availableSeats": 100
        |}
      """.stripMargin)

    val maybeCreateMovieOutput = Json.fromJson[MovieDTO](createMovieRequestJSON).asOpt

    maybeCreateMovieOutput should be(Some(MovieDTO("tt0111161", "screen_123456",100)))


  }

  "MovieId" should "deserialized from JSON correctly " in {

    val maybeMovieIdRequestJSON = Json.parse(
      s"""
        |{
        |  "imdbId": "$randomId",
        |  "screenId": "screen_123456"
        |}
      """.stripMargin)

    val maybeMovieId = Json.fromJson[MovieId](maybeMovieIdRequestJSON).asOpt

    maybeMovieId should be(Some(testMovieId))


  }

  "MovieId" should " fail to deserialized from JSON correctly " in {

    val maybeMovieIdRequestJSON = Json.parse(
      s"""
        |{
        |  "brokenId": "$randomId",
        |  "screenId": "screen_123456",
        |  "availableSeats": 100
        |}
      """.stripMargin)

    val maybeMovieId = Json.fromJson[MovieId](maybeMovieIdRequestJSON).asOpt

    maybeMovieId should be(None)


  }



}


