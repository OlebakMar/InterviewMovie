package controllers

/**
  * Created by rametse on 2017/07/09.
  */
import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent._
import javax.inject.Singleton

@Singleton
class ErrorHandler extends HttpErrorHandler {

  def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
    Future.successful(
      Status(statusCode)(s"""{"errorMsg": "${message}"}""")
    )
  }

  def onServerError(request: RequestHeader, exception: Throwable) = {
    Future.successful(
      InternalServerError("A server error occurred: " + exception.getMessage)
    )
  }
}
