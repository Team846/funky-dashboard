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

  dashboard.datasetGroup("Basic Test").addDataset(new TimeSeriesNumeric("Basic Test")(math.random))

  dashboard.datasetGroup("Text test").addDataset(new TimeTextNumeric("Text test")({
    if (math.random < 0.2) "other message"
    else "message"
  }))

  dashboard.datasetGroup("Multiple List Test").addDataset(new TimeSeriesLists("Multiple Lists")(
    List(math.random(),math.random())
  ))

  dashboard.datasetGroup("Table Test").addDataset(new TimeSeriesTable("Table Test")(
    List.tabulate((math.random * 10).toInt)(i => (math.random.toString, math.random.toString))
  ))

  dashboard.datasetGroup("Snapshot Test").addDataset(new TimeSnapshotNumeric("Snapshot Test") ({
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
