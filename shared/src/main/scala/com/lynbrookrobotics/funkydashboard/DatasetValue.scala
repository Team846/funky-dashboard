package com.lynbrookrobotics.funkydashboard

sealed trait DatasetValue

case class TimeSeriesValue(value: Double)

case class TimeSnapshotValue(value: Option[Double])

case class ImageStreamValue(value: String)
