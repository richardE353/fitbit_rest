package com.fitbit.service

import akka.actor.Props
import com.fitbit.client.{FitbitClient, FitbitJsonProtocol}
import com.fitbit.model.activities._
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.routing._
import spray.util.LoggingContext

import scala.concurrent.Future
import scala.language.implicitConversions
import scala.util.control.NonFatal

object FitbitHttpService {
  def props(client: FitbitClient): Props = Props(new FitbitHttpService(client))
}

class FitbitHttpService(val client: FitbitClient) extends HttpServiceActor with FitBitPaths {
  def receive = runRoute {
    fitbitRoutes
  }
}

trait FitBitPaths extends HttpServiceActor with SprayJsonSupport with DefaultJsonProtocol {
  import DefaultJsonProtocol._
  import spray.json._
  import FitbitJsonProtocol._

  // http://stackoverflow.com/questions/19809984/spray-marshaller-for-futures-not-in-implicit-scope-after-upgrading-to-spray-1-2
  implicit def executionContext = actorRefFactory.dispatcher

  def client: FitbitClient

  implicit def myExceptionHandler(implicit log: LoggingContext): ExceptionHandler =
    ExceptionHandler {
      case NonFatal(e) =>
        requestUri { uri =>
          log.warning("Request to {} could not be handled normally due to: {}", uri, e)
          complete(spray.http.StatusCodes.NotAcceptable, "Fitbit unavailable.")
        }
    }

  val fitbitRoutes = path("api" / "authorize") {
    get {
      complete(requestAuthorization)
    }
  } ~
    path("api" / "authorization") {
      get {
        parameterMap { params =>
          complete(requestToken(params.getOrElse("code", ""), params.getOrElse("state", "")))
        }
      }
    } ~
    path("api" / "activities" / "calories") {
      get {
        headerValueByName("Authorization") { hdrToken =>
          parameterMap { params =>
            complete(client.getActivityDetails(Calories, "today", hdrToken))
          }
        }
      }
    } ~
    path("api" / "activities" / "distance") {
      get {
        headerValueByName("Authorization") { hdrToken =>
          parameterMap { params =>
            complete(client.getActivityDetails(Distance, "today", hdrToken))
          }
        }
      }
    } ~
    path("api" / "activities" / "elevation") {
      get {
        headerValueByName("Authorization") { hdrToken =>
          parameterMap { params =>
            complete(client.getActivityDetails(Elevation, "today", hdrToken))
          }
        }
      }
    } ~
    path("api" / "activities" / "floors") {
      get {
        headerValueByName("Authorization") { hdrToken =>
          parameterMap { params =>
            complete(client.getActivityDetails(Floors, "today", hdrToken))
          }
        }
      }
    } ~
    path("api" / "activities" / "heart") {
      get {
        headerValueByName("Authorization") { hdrToken =>
          parameterMap { params =>
            complete(client.getActivityDetails(Heart, "today", hdrToken))
          }
        }
      }
    } ~
    path("api" / "activities" / "steps") {
      get {
        headerValueByName("Authorization") { hdrToken =>
          parameterMap { params =>
            complete(client.getActivityDetails(Steps, "today", hdrToken))
          }
        }
      }
    } ~
  path("api" / "profile") {
    get {
      headerValueByName("Authorization") { hdrToken =>
        parameterMap { params =>
          complete(client.getResource("/profile.json", hdrToken))
        }
      }
    }
  }

  def requestAuthorization: String = client.authRequestUri

  def requestToken(code: String, state: String): Future[String] = {
    client.getAccessToken(code, state)
  }
}


