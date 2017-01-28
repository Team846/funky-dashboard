package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.{ReactComponentU, TopNode}
import upickle.default._

object Dataset {
  def extract(definition: DatasetDefinition): List[(Double, String)] => ReactComponentU[_, _, _, TopNode] = {
    definition match {
      case DatasetDefinition(_, "time-series-numeric") =>
        values => TimeSeriesNumeric(
          values.map(v => (v._1, read[TimeSeriesValue](v._2)))
        )
      case DatasetDefinition(_, "time-snapshot") =>
        values => TimeSnapshotNumeric(
          values.map(v => (v._1, read[TimeSnapshotValue](v._2)))
        )
      case DatasetDefinition(_, "image-stream") =>
        values => ImageStream(
          values.map(v => read[ImageStreamValue](v._2))
        )
    }
  }
}