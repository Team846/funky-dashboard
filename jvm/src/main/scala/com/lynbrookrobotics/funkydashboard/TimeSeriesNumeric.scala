package com.lynbrookrobotics.funkydashboard

class TimeSeriesNumeric(name: String)(value: => Double) extends Dataset[TimeSeriesValue] {
  override val definition =
    DatasetDefinition(name, "time-series-numeric")

  override def currentValue = TimeSeriesValue(value)
}
