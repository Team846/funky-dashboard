package com.lynbrookrobotics.funkydashboard

import play.api.libs.json.Json

case class DatasetDefinition(name: String, streamType: String)

object DatasetDefinition {
  implicit val format = Json.format[DatasetDefinition]
}