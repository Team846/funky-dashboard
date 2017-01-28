package com.lynbrookrobotics.funkydashboard

class TimeSnapshotNumeric(name: String)(value: => Option[Double]) extends Dataset[TimeSnapshotValue] {
  override val definition = DatasetDefinition(name, "time-snapshot")

  override def currentValue = TimeSnapshotValue(value)
}
