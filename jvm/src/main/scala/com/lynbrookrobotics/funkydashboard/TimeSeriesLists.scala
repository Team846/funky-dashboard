package com.lynbrookrobotics.funkydashboard

class TimeSeriesLists(name: String)(value: => Seq[Double]) extends Dataset[Seq[Double]] {
  override val definition = DatasetDefinition(name, "time-multiple-dataset")

  override def currentValue = value
}