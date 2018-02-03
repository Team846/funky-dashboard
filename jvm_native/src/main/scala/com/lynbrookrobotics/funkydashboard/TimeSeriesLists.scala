package com.lynbrookrobotics.funkydashboard

import argonaut._, Argonaut._, ArgonautShapeless._

class TimeSeriesLists(name: String)(value: => List[Double]) extends Dataset[List[Double]] {
  override val definition = DatasetDefinition(name, "time-multiple-dataset")

  override def currentValue = value
}