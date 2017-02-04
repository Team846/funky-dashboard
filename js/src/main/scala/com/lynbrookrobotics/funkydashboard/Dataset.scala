package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.{ReactComponentU, TopNode}
import upickle.default._

import scala.collection.immutable.Queue

object Dataset {
  def extract(definition: DatasetDefinition, sendData: String => Unit): Queue[(Double, String)] => ReactComponentU[_, _, _, TopNode] = {
    definition match {
      case DatasetDefinition(_, "time-series-numeric") =>
        values => TimeSeriesNumeric(
          values.map(v => (v._1, read[TimeSeriesValue](v._2)))
        )

      case DatasetDefinition(_, "time-series-table") =>
        values => TimeTableNumeric(
          values.map(v => (v._1, read[List[TimeTableValue]](v._2)))
        )

      case DatasetDefinition(_, "time-snapshot") =>
        values => TimeSnapshotNumeric(
          values.map(v => (v._1, read[TimeSnapshotValue](v._2)))
        )

      case DatasetDefinition(_, "image-stream") =>
        values => ImageStream(
          values.map(v => read[ImageStreamValue](v._2))
        )

      case DatasetDefinition(_, "time-text") =>
        values => TimeTextNumeric(
          values.map(v => (v._1, read[TimeTextValue](v._2)))
        )

      case DatasetDefinition(_, "json-editor") => values =>
        JsonEditorDataset(
          values.map(v => (v._1, read[JsonEditorValue](v._2))),
          sendData
        )
    }
  }
}
