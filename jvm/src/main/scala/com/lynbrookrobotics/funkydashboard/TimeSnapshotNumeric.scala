package com.lynbrookrobotics.funkydashboard

class TimeSnapshotNumeric(name: String)(value: => Option[Double]) extends Dataset[List[Double]] {
  override val definition = DatasetDefinition(name, "time-snapshot")

  override def currentValue = value.toList
}
