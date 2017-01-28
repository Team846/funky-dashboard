package com.lynbrookrobotics.funkydashboard

class TimeTextNumeric(name: String)(value: => Double, message: => Option[String]) extends Dataset[TimeTextValue] {
  override val definition =
    DatasetDefinition(name, "time-text")

  override def currentValue = TimeTextValue(value, message)
}
