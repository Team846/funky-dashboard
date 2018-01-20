package com.lynbrookrobotics.funkydashboard

class TimeSeriesLists(name: String)(whatUnits: String)(value: => Seq[Double]) extends Dataset[Seq[Double]] {
  override val definition = DatasetDefinition(name, s"time-multiple-dataset:$whatUnits")

  override def currentValue = value
}