package controllers

import javax.inject._
import model._
import play.api.libs.json.Json
import play.api.mvc._
import server.MovieManagerAPI
import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

@Singleton
class MovieController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val system = ActorSystem("MovieSystem")
  val movieManagerAPI = new MovieManagerAPI(system)
  implicit val timeout = Timeout(15 seconds)


  def registerMovie = Action.async { request =>
    val body = request.body
    val maybeJsonBody = body.asJson

    maybeJsonBody match{
      case Some(jsonBody) =>
        jsonBody.asOpt[MovieDTO] match {
          case Some(movieDTO) =>
            val movieToRegister = movieDTO.toMovie
            println(s"going to register movie now. $movieToRegister")
            val movieResult = movieManagerAPI.registerMovie(movieToRegister)
            movieResult.map { movie =>
              Ok(Json.toJson(movie))
            }.recover{
              case ex  =>
                BadRequest(errorJson(ex.getMessage))
            }
          case None => errorJsonFields
        }

      case None => errorEmptyBody
    }

  }

    def reserveSeatAtMovie = Action.async { request =>
      val body = request.body
      val maybeJsonBody = body.asJson
      maybeJsonBody match{
        case Some(jsonBody) =>
          jsonBody.asOpt[MovieId] match {
            case Some(movieId) =>
              println(s"going to reserve seat at $movieId.")
              val movieResult = movieManagerAPI.reserveSeatAtMovie(movieId)
              movieResult.map { movie =>
                Ok(Json.toJson(movie))
              }.recover{
                case ex  =>
                  BadRequest(errorJson(ex.getMessage))
              }
            case None => errorJsonFields
          }

        case None => errorEmptyBody
      }

    }

  def getMovieDetails = Action.async { request =>
    val body = request.body
    val maybeJsonBody = body.asJson

    maybeJsonBody match{
      case Some(jsonBody) =>
        jsonBody.asOpt[MovieId] match {
          case Some(movieId) =>
            val movieResult = movieManagerAPI.retrieveMovieDetails(movieId)
            movieResult.map { movie =>
              Ok(Json.toJson(movie))
            }.recover{
              case ex  =>
                BadRequest(errorJson(ex.getMessage))
            }
          case None => errorJsonFields
        }

      case None => errorEmptyBody
    }

  }

  def errorJson(errorMsg: String) = Json.parse(s"""{"errorMsg": "$errorMsg"}""")
  def errorEmptyBody = Future(BadRequest(errorJson("Empty body : {}")))
  def errorJsonFields = Future(BadRequest(errorJson(s"""Please check input body for missing or incorrect field name""")))

}