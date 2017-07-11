package controllers

import akka.actor.{Actor, ActorSystem}
import akka.stream.ActorMaterializer
import com.novus.salat.dao.{BaseDAOMethods, SalatDAO}
import model._
import org.bson.types.ObjectId
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._
import play.api.test.Helpers._
import play.test.WithApplication
import testFixtures.MovieTestFixture
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterAll
import org.specs2._
import play.api.{Application, Play}
import play.api.inject.guice.GuiceApplicationBuilder
import server.{MovieManagerAPI, MovieManagerActor, MovieStorageAPI}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import play.api.inject.bind
import play.api.libs.json.Json

import scala.concurrent.duration._


/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */

class MovieControllerSpec extends PlaySpec
  with GuiceOneAppPerTest
  with Injecting
  with MovieTestFixture
  with MockitoSugar
  with BeforeAndAfterAll{

  override def beforeAll(): Unit = {
  }

  override def afterAll(): Unit = {
    MovieDAO.remove(MovieQueryParams(_id = Some(testMovieDTO.toMovie._id)).toDBObject)
  }

  "MovieController" should {

    "register movie via POST" in {
      val request = FakeRequest(POST, "/movie").withBody(testJsonMovieDTO)
      val home = route(app,request).get
      status(home) mustBe OK
      contentAsJson(home) mustBe testJsonMovieDTO
    }

    "get movie details via GET" in {
      val request = FakeRequest(GET, "/movie").withBody(testJsonMovieId)
      val home = route(app,request).get
      status(home) mustBe OK
      contentAsJson(home) mustBe testJsonMovieDTO
    }

    "reserve a seat at the movie via PUT" in {
      val request = FakeRequest(PUT, "/movie").withBody(testJsonMovieId)
      val home = route(app,request).get
      status(home) mustBe OK
      contentAsJson(home) mustBe Json.toJson(testMovieDTO.copy(reservedSeats = Some(1)))
    }

  }

}
