package com.lynbrookrobotics.funkydashboard

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, RoutingSettings}

object Main extends App {
  implicit val system = ActorSystem("funky-dashboard")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val routingSettings = RoutingSettings.default

  Http().bindAndHandle(Route.handlerFlow(FunkyDashboard.route), "0.0.0.0", 8080)

  FunkyDashboard.datasetGroup("test").addDataset(new TimeSeriesNumeric("random")(math.random))
}
