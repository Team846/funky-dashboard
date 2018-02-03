package com.lynbrookrobotics.funkydashboard

class TimeText(name: String)(message: => String) extends Dataset[String] {
  override val definition =
    DatasetDefinition(name, "time-text")

  override def currentValue = message
}
