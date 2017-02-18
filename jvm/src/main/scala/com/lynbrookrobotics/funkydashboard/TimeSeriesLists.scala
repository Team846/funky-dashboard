package com.lynbrookrobotics.funkydashboard

class TimeSeriesLists(name: String)(value: => List[Double]) extends Dataset[TimeSeriesListValue] {
  override val definition = DatasetDefinition(name, "time-multiple-dataset")

  override def currentValue = TimeSeriesListValue(value)
}