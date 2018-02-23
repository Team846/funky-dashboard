package com.lynbrookrobotics.funkydashboard

class JsonEditor(name: String)(json: => String, onUpdate: String => Unit) extends Dataset[String] {
  override val definition =
    DatasetDefinition(name, "json-editor")

  override def currentValue = json

  override def handleIncomingData(data: String): Unit = {
    onUpdate(data)
  }
}
