package com.lynbrookrobotics.funkydashboard

import play.api.libs.json.Json

case class DatasetGroupDefinition(name: String, datasets: List[DatasetDefinition])

object DatasetGroupDefinition {
  implicit val format = Json.format[DatasetGroupDefinition]
}
