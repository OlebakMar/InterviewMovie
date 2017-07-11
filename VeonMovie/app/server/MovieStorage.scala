package server


import akka.actor.{Actor, _}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import model._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
//import context.dispatcher
/**
  * Created by rametse on 2017/07/08.
  */

class MovieStorageAPI(actorSystem: ActorSystem) {

  val movieStorageActor = actorSystem.actorOf(Props[MovieStorageActor], name = "MovieStorageActor")
  implicit val timeout = Timeout(15 seconds)

  def saveMovie(movie: Movie): Future[Movie] ={
    (movieStorageActor ? SaveMovie(movie)).mapTo[Movie]
  }

  def updateMovie(movie: Movie) : Future[Movie] = {
    (movieStorageActor ? UpdateMovie(movie)).mapTo[Movie]
  }

  def retrieveMovieDetails(movieId: MovieId) : Future[Movie] = {
    (movieStorageActor ? RetrieveMovie(movieId)).mapTo[Movie]
  }
}

private class MovieStorageActor extends Actor{

  def receive = {
    case SaveMovie(movie: Movie) =>
      println(s"got message to save movie. $self")
      /*
      * salat does not offer support for compound keys, Movie has the compound key imdbId & screenId
      * because of this, the insert() will never return a ID. so we will treat it to always work.
      *
      * todo: if time allows, use mongo ObjectId to key the object in db and just create a unique index on imdbId & screenId
      * */
      val maybeMovie = MovieDAO.find(MovieQueryParams(_id = Some(movie._id)).toDBObject).toList.headOption

      val futureMovie = maybeMovie match {
        case Some(movie) => Future.failed(new RuntimeException(s"Movie is already registered imdbId: ${movie._id.imdbId} , screenId: ${movie._id.screenId}"))
        case None =>
          MovieDAO.insert(movie)
          Future(movie)
      }
      println(s"Movie has been saved. $self")

      futureMovie pipeTo sender

    case UpdateMovie(movie: Movie) =>
      println(s"got message to update movie. $self")
      val writeResult = MovieDAO.update(MovieQueryParams(_id = Some(movie._id)).toDBObject,MovieDAO.toDBObject(movie))

     val futureMovie = writeResult.isUpdateOfExisting match {
        case true => Future(movie)
        case false => Future.failed(new RuntimeException(s"Update of movie or seat reservation failed."))
      }

      println(s"movie updated. $self")
      futureMovie pipeTo sender

    case RetrieveMovie(movieId:MovieId) =>
      println(s"got message to get movie. $self")

      val maybeMovie = MovieDAO.find(MovieQueryParams(_id = Some(movieId)).toDBObject).toList.headOption

      val futureMovie = maybeMovie match {
        case Some(movie) => Future(movie)
        case None => Future.failed(new RuntimeException(s"Could not find movie with imdbId: ${movieId.imdbId} , screenId: ${movieId.screenId}"))
      }

      futureMovie pipeTo sender
  }

}
