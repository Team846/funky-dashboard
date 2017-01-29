package com.lynbrookrobotics.funkydashboard

class TimeTextNumeric(name: String)(message: => String) extends Dataset[TimeTextValue] {
  override val definition =
    DatasetDefinition(name, "time-text")

  override def currentValue = TimeTextValue(message)
}
