package com.lynbrookrobotics.funkydashboard

class TimeSeriesTable(name: String)(value: => List[(String, String)]) extends Dataset[List[TimeTableValue]] {
  override val definition =
    DatasetDefinition(name, "time-series-table")

  override def currentValue = value.map(v => TimeTableValue(v._1, v._2))
}
