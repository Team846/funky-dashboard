package com.lynbrookrobotics.funkydashboard

import play.api.libs.json.{Format, Json}

case class TimedValue[+T](time: Long, value: T)

object TimedValue {
  implicit def format[T: Format] = Json.format[TimedValue[T]]
}
