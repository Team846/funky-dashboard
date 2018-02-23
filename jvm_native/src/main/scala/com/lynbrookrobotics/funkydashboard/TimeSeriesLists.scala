package com.lynbrookrobotics.funkydashboard

import argonaut._, Argonaut._, ArgonautShapeless._

class TimeSeriesLists(name: String)(whatUnits: String)(value: => List[Double]) extends Dataset[List[Double]] {
  override val definition = DatasetDefinition(name, s"time-multiple-dataset:$whatUnits")

  override def currentValue = value
}