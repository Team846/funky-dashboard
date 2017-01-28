package com.lynbrookrobotics.funkydashboard

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.settings.RoutingSettings

import scala.io.StdIn

object Main extends App {
  implicit val system = ActorSystem("funky-dashboard")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  implicit val routingSettings = RoutingSettings.default

  val dashboard = new FunkyDashboard

  Http().bindAndHandle(Route.handlerFlow(dashboard.route), "0.0.0.0", 8080)

  dashboard.datasetGroup("test").addDataset(new TimeSeriesNumeric("random")(math.random))
  dashboard.datasetGroup("test2").addDataset(new TimeSeriesNumeric("randomer")(math.random))
  dashboard.datasetGroup("TestSnapshot").addDataset(new TimeSnapshotNumeric("randomers") ({
    if (math.random < 0.05) None else {
      Some(math.random)
    }
  }))
  StdIn.readLine()

  system.terminate()
}
