package com.lynbrookrobotics.funkydashboard

import org.scalajs.dom._

import scala.scalajs.js

abstract class Dataset[-U](name: String) {
  val dataDisplay: html.Element
  lazy val card: html.Element = {
    val baseElement = document.createElement("div").asInstanceOf[html.Div]
    baseElement.className = "mdl-card mdl-shadow--16dp mdl-cell mdl-cell--6-col"
    baseElement.style.minHeight = "0px"
    baseElement.appendChild(dataDisplay)

    val cardTitle = document.createElement("div").asInstanceOf[html.Div]
    cardTitle.className = "mdl-card__title"
    cardTitle.innerHTML = name
    baseElement.appendChild(cardTitle)

    baseElement
  }

  def update(newData: js.Any): Unit = {
    updateDisplay(newData.asInstanceOf[U])
  }

  def updateDisplay(newData: U)
}

object Dataset {
  def extract(definition: DatasetDefinition): Dataset[Nothing] = {
    definition.`type` match {
      case "time-series-numeric" =>
        new TimeSeriesNumeric(definition.name, definition.properties.asInstanceOf)
    }
  }
}