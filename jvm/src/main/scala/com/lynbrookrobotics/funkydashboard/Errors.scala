package com.lynbrookrobotics.funkydashboard

import java.util

class Errors(name: String) extends Dataset[String] {
  override val definition =
    DatasetDefinition(name, "errorstream")

  val message = new util.LinkedList[String]()

  override def currentValue = {
    val line = message.poll()
    if(line==null) "" else line
  }

  def pushErr(err: String): Unit = {
    message.offer(err)
  }
}
