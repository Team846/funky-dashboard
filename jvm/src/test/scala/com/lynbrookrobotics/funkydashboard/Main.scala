package com.lynbrookrobotics.funkydashboard

import scala.io.StdIn

object Main extends App {
  val dashboard = new FunkyDashboard(125, 8080)

  dashboard.datasetGroup("Basic Test").addDataset(new TimeSeriesNumeric("Basic Test")(math.random))

  dashboard.datasetGroup("Text test").addDataset(new TimeText("Text test")({
    if (math.random < 0.2) "other message"
    else "message"
  }))

  dashboard.datasetGroup("Multiple List Test").addDataset(new TimeSeriesLists("Multiple Lists")(
    List(math.random(),math.random())
  ))

  dashboard.datasetGroup("Table Test").addDataset(new TableDataset("Table Test")(
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

  dashboard.start()

  StdIn.readLine()

  println("terminating!")

  dashboard.stop()
}
