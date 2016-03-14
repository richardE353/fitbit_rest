package com.fitbit.service

import akka.actor.Props
import com.fitbit.client.FitbitClient
import com.fitbit.model.activities._
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.routing._
import spray.util.LoggingContext

import scala.concurrent.Future
import scala.language.implicitConversions
import scala.util.control.NonFatal

object FitbitHttpService {
  def props(client: FitbitClient, responseType: String): Props = Props(new FitbitHttpService(client, responseType))
}

class FitbitHttpService(val client: FitbitClient, val responseType: String) extends HttpServiceActor with FitBitPaths {
  def receive = runRoute {
    fitbitRoutes
  }
}

trait FitBitPaths extends HttpServiceActor with SprayJsonSupport with DefaultJsonProtocol {
  // http://stackoverflow.com/questions/19809984/spray-marshaller-for-futures-not-in-implicit-scope-after-upgrading-to-spray-1-2
  implicit def executionContext = actorRefFactory.dispatcher

  def client: FitbitClient

  def responseType: String

  implicit def myExceptionHandler(implicit log: LoggingContext): ExceptionHandler =
    ExceptionHandler {
      case NonFatal(e) =>
        requestUri { uri =>
          log.warning("Request to {} could not be handled normally due to: {}", uri, e)
          complete(spray.http.StatusCodes.NotAcceptable, "Fitbit unavailable.")
        }
    }

  val fitbitRoutes = authorizationRoutes ~ activityRoutes ~ profilePath ~ sleepPath ~ foodPath

  def authorizationRoutes = path("api" / "authorize") {
    get {
      complete(requestAuthorization)
    }
  } ~ path("api" / "authorization") {
    get {
      parameterMap { params =>
        responseType match {
          case "token" => complete("extract access_token from browser location")
          case _ => complete(requestToken(params.getOrElse("code", ""), params.getOrElse("state", "")))
        }
      }
    }
  }


  def activityRoutes = pathPrefix("api" / "activities") {
    get {
      headerValueByName("Authorization") { hdrToken =>
        parameterMap { params =>
          val selectedDate = params.getOrElse("date", "today")

          pathPrefix("calories") {
            complete(client.getActivityDetails(Calories, selectedDate, hdrToken, params))
          } ~
            pathPrefix("distance") {
              complete(client.getActivityDetails(Distance, selectedDate, hdrToken, params))
            } ~
            pathPrefix("elevation") {
              complete(client.getActivityDetails(Elevation, selectedDate, hdrToken, params))
            } ~
            pathPrefix("floors") {
              complete(client.getActivityDetails(Floors, selectedDate, hdrToken, params))
            } ~
            pathPrefix("heart") {
              complete(client.getActivityDetails(Heart, selectedDate, hdrToken, params))
            } ~
            pathPrefix("steps") {
              complete(client.getActivityDetails(Steps, selectedDate, hdrToken, params))
            }
        }
      }
    }
  }

  def profilePath = path("api" / "profile") {
    get {
      headerValueByName("Authorization") {
        hdrToken =>
          complete(client.getResource("/profile.json", hdrToken))
      }
    }
  }


  def sleepPath = path("api" / "sleep" / "date" / Rest) {
    dateString =>
      get {
        headerValueByName("Authorization") {
          hdrToken =>
            complete(client.getResource("/sleep/date/" + dateString + ".json", hdrToken))
        }
      }
  }

  def foodPath = path("api" / "foods" / "log" / "date" / Rest) {
    dateString =>
      get {
        headerValueByName("Authorization") {
          hdrToken =>
            complete(client.getResource("/foods/log/date/" + dateString + ".json", hdrToken))
        }
      }
  }

  def requestAuthorization: String = client.authRequestUri

  def requestToken(code: String, state: String): Future[String] = client.getAccessToken(code, state)
}


