package com.lynbrookrobotics.funkydashboard

case class TimedValue[+T](time: Long, value: T)
