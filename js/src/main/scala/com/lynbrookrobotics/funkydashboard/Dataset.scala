package com.lynbrookrobotics.funkydashboard

import me.shadaj.slinky.core.facade.ReactElement
import play.api.libs.json.Json

import scala.collection.immutable.Queue

object Dataset {
  def extract(definition: DatasetDefinition, sendData: String => Unit): Queue[TimedValue[String]] => ReactElement = {
    definition match {
      case DatasetDefinition(_, tpe) if tpe.startsWith("time-series-numeric") =>
        values => TimeSeriesNumeric(
          values.map(v => TimedValue(v.time, Json.parse(v.value).as[Double])),
          tpe.drop(20)
        )

      case DatasetDefinition(_, tpe) if tpe.startsWith("time-multiple-dataset") =>
        values => MultipleTimeSeriesNumeric(
          values.map(v => TimedValue(v.time, Json.parse(v.value).as[List[Double]])),
          tpe.drop(22)
        )

      case DatasetDefinition(_, tpe) if tpe.startsWith("table") =>
        values => TableDataset(
          values.map(v => TimedValue(v.time, Json.parse(v.value).as[List[TablePair]]))
        )

      case DatasetDefinition(_, tpe) if tpe.startsWith("time-snapshot") =>
        values => TimeSnapshotNumeric(
          values.map(v => TimedValue(v.time, Json.parse(v.value).as[List[Double]].headOption)),
          tpe.drop(14)
        )

      case DatasetDefinition(_, tpe) if tpe.startsWith("image-stream") =>
        values => ImageStream(
          values.map(v => Json.parse(v.value).as[String])
        )

      case DatasetDefinition(_, tpe) if tpe.startsWith("time-text") =>
        values => TimeText(
          values.map(v => TimedValue(v.time, Json.parse(v.value).as[String]))
        )

      case DatasetDefinition(_, tpe) if tpe.startsWith("json-editor") =>
        values => JsonEditorDataset(
          values.map(v => TimedValue(v.time, Json.parse(v.value).as[String])),
          sendData
        )
    }
  }
}
