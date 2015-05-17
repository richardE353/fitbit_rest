package com.fitbit.client

import spray.json.DefaultJsonProtocol

object FitbitJsonProtocol extends DefaultJsonProtocol {
  implicit val tokenFormat = jsonFormat4(FitbitToken.apply)
  implicit val tokenRequestFormat = jsonFormat4(FitbitTokenRequest.apply)
}

case class FitbitToken(access_token: String, expires_in: Int, refresh_token: String, token_type: String)
case class FitbitTokenRequest(code: String, grant_type: String, client_id: String, redirect_uri: String)

