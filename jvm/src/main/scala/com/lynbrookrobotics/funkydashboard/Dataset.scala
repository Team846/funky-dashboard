package com.lynbrookrobotics.funkydashboard

import upickle.default._

abstract class Dataset[T](implicit valueWriter: Writer[T]) {
  val definition: DatasetDefinition

  def currentValue: T

  def handleIncomingData(data: String): Unit = {
    println(s"Unhandled incoming data: $data")
  }

  def currentValueJSON: String = {
    write(currentValue)
  }
}
