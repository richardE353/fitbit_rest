package com.fitbit.service

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.fitbit.client.FitbitClient
import com.typesafe.config.{Config, ConfigFactory}
import spray.can.Http

import scala.concurrent.duration._

object BootFitBitSvc extends App {
  val svcConf: Config = ConfigFactory.parseResources("application.conf").resolve

  implicit val system = ActorSystem("fitbit-service")
  implicit val timeout = Timeout(5.seconds)

  val config = initFitbitConfig(svcConf)
  val client = new FitbitClient(config)(system)

  val service = system.actorOf(FitbitHttpService.props(client), "fitbit-service")
  IO(Http) ? Http.Bind(service, interface = config.host, port = config.port)

  def initFitbitConfig(appConf: Config): FitbitConfig = {
    val clientId = appConf.getString("fitbit.clientId")
    val secret = appConf.getString("fitbit.clientSecret")
    val protocol = appConf.getString("fitbit.protocol")
    val host = appConf.getString("fitbit.host")
    val port = appConf.getInt("fitbit.port")

    FitbitConfig(clientId, secret, protocol, host, port)
  }
}
