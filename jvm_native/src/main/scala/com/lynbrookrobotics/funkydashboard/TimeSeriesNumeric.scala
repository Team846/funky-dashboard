package com.lynbrookrobotics.funkydashboard

class TimeSeriesNumeric(name: String)(whatUnit: String)(value: => Double) extends Dataset[Double] {
  override val definition =
    DatasetDefinition(name, s"time-series-numeric:$whatUnit")

  override def currentValue = value
}
