package com.lynbrookrobotics.funkydashboard

import play.api.libs.json.{Json, Writes}

abstract class Dataset[T](implicit valueWriter: Writes[T]) {
  val definition: DatasetDefinition

  def currentValue: T

  def handleIncomingData(data: String): Unit = {
    println(s"Unhandled incoming data: $data")
  }

  def currentValueJSON: String = {
    Json.toJson(currentValue).toString()
  }
}
