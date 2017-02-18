package com.lynbrookrobotics.funkydashboard

sealed trait DatasetValue

case class TimeSeriesValue(value: Double)

case class TimeSnapshotValue(value: Option[Double])

case class ImageStreamValue(value: String)

case class TimeTableValue(key: String, value: String)

case class TimeTextValue(message: String)

case class JsonEditorValue(json: String)

case class TimeSeriesListValue(value: List[Double])
