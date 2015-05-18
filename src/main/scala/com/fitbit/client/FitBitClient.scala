package com.fitbit.client

import java.net.URLEncoder
import java.util.UUID

import com.fitbit.common.{Second, QuarterHour, Minute, IntraDayResolution}
import com.fitbit.model.activities.{Heart, IntraDayTimeSeries, TimeSeriesType}
import akka.actor.ActorSystem
import akka.util.Timeout
import com.fitbit.service.FitbitConfig
import spray.client.pipelining._
import spray.http.{FormData, OAuth2BearerToken, HttpRequest}
import spray.httpx.SprayJsonSupport
import spray.httpx.encoding.{Deflate, Gzip}
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class FitbitClient(config: FitbitConfig)(implicit system: ActorSystem) {
  private implicit val timeout: Timeout = 15.seconds
  val apiAuthState = UUID.randomUUID()
  val encodedRedirect = URLEncoder.encode(config.apiRedirectUri, "UTF-8")
  val authScope = "activity%20nutrition%20heartrate%20location%20nutrition%20profile%20settings%20sleep%20social%20weight"

  val clientId = config.clientId
  val authRequestUri = s"https://www.fitbit.com/oauth2/authorize?client_id=$clientId&response_type=code&state=$apiAuthState&redirect_uri=$encodedRedirect&scope=$authScope"

  def getActivityDetails(seriesType: TimeSeriesType, selectedDate: String, token: String, params: Map[String, String] = Map()): Future[String] = {
    val res = params.getOrElse("resolution", "1min")
    val resolution = selectResolution(seriesType, res)

    val startTime = params.getOrElse("start-time", "")
    val endTime = params.getOrElse("end-time", "")

    val basePath = seriesType.endpoint + s"/date/${selectedDate}/1d/${resolution.pathValue}"

    val resourcePath =
      if (startTime.isEmpty || endTime.isEmpty)
        basePath + ".json"
      else
        basePath + s"/time/${startTime}/${endTime}.json"

    getResource(resourcePath, token)
  }

  def getResource(userPath: String, token: String = ""): Future[String] = {
    val targetURL = "https://api.fitbit.com/1/user/-" + userPath
    val baseToken = cleanToken(token)
    val qry: HttpRequest = Get(targetURL) ~> addCredentials(OAuth2BearerToken(baseToken))

    val pipeline: HttpRequest => Future[String] = sendReceive ~> unmarshal[String]
    pipeline(qry)
  }

  def getAccessToken(authCode: String, state: String): Future[String] = {
    val pipeline: HttpRequest => Future[String] = (
      encode(Gzip)
        ~> addHeader("Authorization", "Basic " + config.authorizationHeader)
        ~> sendReceive
        ~> decode(Deflate)
        ~> unmarshal[String]
      )
    pipeline { Post("https://api.fitbit.com/oauth2/token", tokenFormData(authCode)) }
  }


  protected def cleanToken(token: String): String = token.replace("Bearer ", "")

  protected def tokenFormData(authCode: String): FormData = {
    FormData(Seq(
      "code" -> authCode,
      "client_id" -> config.clientId,
      "grant_type" -> "authorization_code",
      "redirect_uri" -> config.apiRedirectUri
    ))
  }

  protected def selectResolution(seriesType: TimeSeriesType, res: String): IntraDayResolution = {
    val candidate = res match {
      case "1sec" => Second
      case "1min" => Minute
      case "15min" => QuarterHour
    }

    if ((seriesType != Heart) && (candidate == Second)) Minute else candidate
  }
}
