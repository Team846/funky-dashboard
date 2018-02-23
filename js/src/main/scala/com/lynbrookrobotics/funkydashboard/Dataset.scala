package com.lynbrookrobotics.funkydashboard

import slinky.core.facade.ReactElement
import argonaut._, Argonaut._, ArgonautShapeless._
import scala.collection.immutable.Queue

object Dataset {
  def extract(definition: DatasetDefinition, sendData: String => Unit): Queue[TimedValue[String]] => ReactElement = {
    definition match {
      case DatasetDefinition(_, tpe) if tpe.startsWith("time-series-numeric") =>
        values => TimeSeriesNumeric(
          values.map(v => TimedValue(v.time, v.value.decodeOption[Double].get)),
          tpe.drop(20)

        )

      case DatasetDefinition(_, tpe) if tpe.startsWith("time-multiple-dataset") =>
        values => MultipleTimeSeriesNumeric(
          values.map(v => TimedValue(v.time, v.value.decodeOption[List[Double]].get)),
          tpe.drop(22)

        )

      case DatasetDefinition(_, tpe) if tpe.startsWith("table") =>
        values => TableDataset(
          values.map(v => TimedValue(v.time, v.value.decodeOption[List[TablePair]].get))
        )

      case DatasetDefinition(_, tpe) if tpe.startsWith("time-snapshot") =>
        values => TimeSnapshotNumeric(
          values.map(v => TimedValue(v.time, v.value.decodeOption[List[Double]].get.headOption)),
          tpe.drop(14)
        )

      case DatasetDefinition(_, tpe) if tpe.startsWith("image-stream") =>
        values => ImageStream(
          values.map(v => v.value.decodeOption[String].get)
        )

      case DatasetDefinition(_, tpe) if tpe.startsWith("time-text") =>
        values => TimeText(
          values.map(v => TimedValue(v.time, v.value.decodeOption[String].get))
        )

      case DatasetDefinition(_, tpe) if tpe.startsWith("json-editor") =>
        values => JsonEditorDataset(
          values.map(v => TimedValue(v.time, v.value.decodeOption[String].get)),
          sendData
        )
    }
  }
}
