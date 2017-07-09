package server

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import model._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._


/**
  * Created by rametse on 2017/07/08.
  */

class MovieManagerAPI(actorSystem: ActorSystem) {

  val movieManagerActor = actorSystem.actorOf(Props[MovieManagerActor], name = "MovieManagerActor")
  implicit val timeout = Timeout(15 seconds)

  def registerMovie(movie: Movie) : Future[Movie] = {
    (movieManagerActor ? RegisterMovie(movie)).mapTo[Movie]
  }

  def reserveSeatAtMovie(movieId: MovieId): Future[Movie] = {
    (movieManagerActor ? ReserveSeatAtMovie(movieId)).mapTo[Movie]
  }

  def retrieveMovieDetails(movieId: MovieId) : Future[Movie] = {
    (movieManagerActor ? RetrieveMovie(movieId)).mapTo[Movie]
  }
}

class MovieManagerActor extends Actor{

  val movieStorageAPI = new MovieStorageAPI(context.system)//MovieStorageAPI


  def receive = {
    case RegisterMovie(movie: Movie) =>

      //save movie in the DB
      println(s"got message to save movie. $self")
      //movieStorage ! SaveMovie(movie: Movie)
      val futureMovie = movieStorageAPI.saveMovie(movie)
      println(s"Movie has been saved. $self")
      futureMovie pipeTo sender

    case ReserveSeatAtMovie(movieId: MovieId)=>
      //get the movie from the DB
      println(s"got message to reserve seat $self")
      val futureMovieWithSeatReserved = for {
        movieFromDB           <- movieStorageAPI.retrieveMovieDetails(movieId)
        canReserveSeat         = canReserveSeatAtMovie(movieFromDB)
        movieWithSeatReserved <- if (canReserveSeat) reserveSeat(movieFromDB) else
                                    Future.failed(new RuntimeException("No seats available"))
      }yield movieWithSeatReserved

      futureMovieWithSeatReserved pipeTo sender

    case RetrieveMovie(movieId: MovieId)=>
      //get the movie from the DB
      println(s"got message to get movie. Will forward it to StorageActor $self")
      val futureMovie = movieStorageAPI.retrieveMovieDetails(movieId)
      println(s"got message to get movie. movie was retrieved $self")
      futureMovie pipeTo sender
  }

  def reserveSeat(movie: Movie) : Future[Movie] = {
    println(s"reserve seat $self")
    val updatedMovie = movie.copy(reservedSeats = movie.reservedSeats+1)
    movieStorageAPI.updateMovie(updatedMovie)
  }

  def canReserveSeatAtMovie(movie: Movie): Boolean = {
    println(s"checking seat availability $self")
    if(movie.hasAvailableSeats){
      return true
    }else{
      false
    }
  }

}
