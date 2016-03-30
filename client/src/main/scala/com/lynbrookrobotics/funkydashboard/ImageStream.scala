package com.lynbrookrobotics.funkydashboard

import org.scalajs.dom._

import scala.scalajs.js
import org.scalajs.jquery._

import com.lynbrookrobotics.flot.Flot._

class ImageStream(name: String) extends Dataset[String](name) {
  val image = document.createElement("img").asInstanceOf[html.Image]
  image.style.width = "100%"

  override val dataDisplay: html.Element = document.createElement("div").asInstanceOf[html.Div]
  dataDisplay.appendChild(image)

  override def updateDisplay(newData: String): Unit = {
    image.src = s"data:image/png;base64,$newData"
  }
}
