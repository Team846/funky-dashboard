package com.lynbrookrobotics.funkydashboard

class TimeSeriesNumeric(name: String)(value: => Double) extends Dataset[TimeSeriesValue] {
  override val definition =
    DatasetDefinition("", "time-series-numeric")

  override def currentValue = TimeSeriesValue(value)
}
