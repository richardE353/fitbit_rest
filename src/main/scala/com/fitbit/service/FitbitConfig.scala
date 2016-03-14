package com.fitbit.service

import com.google.common.io.BaseEncoding

case class FitbitConfig(clientId: String, clientSecret: String, protocol: String, host: String, port: Int, responseType: String) {
  def apiRedirectUri = s"$protocol://$host:$port/api/authorization"

  def authorizationHeader: String = {
    val contents = clientId + ":" + clientSecret
    BaseEncoding.base64.encode(contents.getBytes)
  }
}
