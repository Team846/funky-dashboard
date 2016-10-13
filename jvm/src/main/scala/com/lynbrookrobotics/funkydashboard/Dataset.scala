package com.lynbrookrobotics.funkydashboard

import upickle.default._

abstract class Dataset[T](implicit valueWriter: Writer[T]) {
  val definition: DatasetDefinition

  def currentValue: T

  def currentValueJSON: String = {
    write(currentValue)
  }
}
