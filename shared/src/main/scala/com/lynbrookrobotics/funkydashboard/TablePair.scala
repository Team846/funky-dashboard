package com.lynbrookrobotics.funkydashboard

import play.api.libs.json.Json

case class TablePair(key: String, value: String)

object TablePair {
  implicit val format = Json.format[TablePair]
}
