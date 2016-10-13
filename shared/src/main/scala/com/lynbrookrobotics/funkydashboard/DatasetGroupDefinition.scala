package com.lynbrookrobotics.funkydashboard

import upickle.default._

case class DatasetGroupDefinition(name: String, definitionStrings: List[String]) {
  lazy val datasets = definitionStrings.map(s => read[DatasetDefinition](s))
}
