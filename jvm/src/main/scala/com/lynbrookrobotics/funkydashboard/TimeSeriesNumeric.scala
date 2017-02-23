package com.lynbrookrobotics.funkydashboard

class TimeSeriesNumeric(name: String)(value: => Double) extends Dataset[Double] {
  override val definition =
    DatasetDefinition(name, "time-series-numeric")

  override def currentValue = value
}
