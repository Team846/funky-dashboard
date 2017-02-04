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

  dashboard.datasetGroup("testText").addDataset(new TimeTextNumeric("randomers")({
    if (math.random < 0.2) "other message"
    else "message"
  }))

  dashboard.datasetGroup("testTable").addDataset(new TimeSeriesTable("randomers")(
    List.tabulate((math.random * 10).toInt)(i => (math.random.toString, math.random.toString))
  ))

  dashboard.datasetGroup("TestSnapshot").addDataset(new TimeSnapshotNumeric("randomers") ({
    if (math.random < 0.05) None else {
      Some(math.random)
    }
  }))

  var currentJSON = """{ "hello": "hi" }"""

  dashboard.datasetGroup("JsonEditor").addDataset(new JsonEditor("jsonvalue") (
    currentJSON,
    s => {
      currentJSON = s
      println(s"updated json, new value: $s")
    }
  ))

  StdIn.readLine()

  system.terminate()
}
