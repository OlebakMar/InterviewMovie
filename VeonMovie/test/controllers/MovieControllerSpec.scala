package controllers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._
import testFixtures.MovieTestFixture

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class MovieControllerSpec extends PlaySpec
  with GuiceOneAppPerTest
  with Injecting
  with MovieTestFixture{

  //implicit val timeout = Timeout(15 seconds)
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  "MovieController POST" should {

    "register movie" ignore {
      val request = FakeRequest(POST, "/movie").withBody(testJsonMovie)
      val home = route(app, request).get

      status(home) mustBe OK
    }
  }

  "MovieController GET" ignore {

    "get movie details" in {
      val request = FakeRequest(GET, "/movie").withBody(testJsonMovieId)
      val home = route(app, request).get

      status(home) mustBe OK
    }
  }

  "MovieController PUT" ignore {

    "reserve a movie seat" in {
      val request = FakeRequest(PUT, "/movie").withBody(testJsonMovieId)
      val home = route(app, request).get

      status(home) mustBe OK
    }
  }
}
