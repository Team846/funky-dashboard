package com.lynbrookrobotics.funkydashboard

import slinky.core.facade.ReactElement
import argonaut._, Argonaut._, ArgonautShapeless._

import scala.collection.immutable.Queue

object Dataset {
  def extract(definition: DatasetDefinition, sendData: String => Unit): Queue[TimedValue[String]] => ReactElement = {
    definition match {
      case DatasetDefinition(_, "time-series-numeric") =>
        values => TimeSeriesNumeric(
          values.map(v => TimedValue(v.time, v.value.decodeOption[Double].get))
        )

      case DatasetDefinition(_, "time-multiple-dataset") =>
        values => MultipleTimeSeriesNumeric(
          values.map(v => TimedValue(v.time, v.value.decodeOption[List[Double]].get))
        )

      case DatasetDefinition(_, "table") =>
        values => TableDataset(
          values.map(v => TimedValue(v.time, v.value.decodeOption[List[TablePair]].get))
        )

      case DatasetDefinition(_, "time-snapshot") =>
        values => TimeSnapshotNumeric(
          values.map(v => TimedValue(v.time, v.value.decodeOption[List[Double]].get.headOption))
        )

      case DatasetDefinition(_, "image-stream") =>
        values => ImageStream(
          values.map(v => v.value.decodeOption[String].get)
        )

      case DatasetDefinition(_, "time-text") =>
        values => TimeText(
          values.map(v => TimedValue(v.time, v.value.decodeOption[String].get))
        )

      case DatasetDefinition(_, "json-editor") => values =>
        JsonEditorDataset(
          values.map(v => TimedValue(v.time, v.value.decodeOption[String].get)),
          sendData
        )
    }
  }
}
