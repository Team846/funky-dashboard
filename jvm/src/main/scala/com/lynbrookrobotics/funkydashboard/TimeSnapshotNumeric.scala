package com.lynbrookrobotics.funkydashboard

class TimeSnapshotNumeric(name: String)(whatUnit: String)(value: => Option[Double]) extends Dataset[List[Double]] {
  override val definition = DatasetDefinition(name, s"time-snapshot:$whatUnit")

  override def currentValue = value.toList
}
