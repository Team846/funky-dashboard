package com.lynbrookrobotics.funkydashboard

import argonaut._, Argonaut._, ArgonautShapeless._

abstract class Dataset[T](implicit valueWriter: EncodeJson[T]) {
  val definition: DatasetDefinition

  def currentValue: T

  def handleIncomingData(data: String): Unit = {
    println(s"Unhandled incoming data: $data")
  }

  def currentValueJSON: String = {
    currentValue.jencode.toString()
  }
}
