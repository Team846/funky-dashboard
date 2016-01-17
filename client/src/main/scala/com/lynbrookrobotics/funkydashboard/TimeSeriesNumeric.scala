package com.lynbrookrobotics.funkydashboard

import org.scalajs.dom._

import scala.scalajs.js
import org.scalajs.jquery._

import com.lynbrookrobotics.flot.Flot._

trait TimeSeriesNumericProperties extends js.Object {
  val numberType: String = js.native
}

class TimeSeriesNumeric(name: String, properties: TimeSeriesNumericProperties) extends Dataset[String](name) {
  val textDisplay = document.createElement("h4").asInstanceOf[html.Heading]
  textDisplay.style.textAlign = "center"

  val chartHolder = document.createElement("div").asInstanceOf[html.Div]
  chartHolder.style.height = "300px"
  chartHolder.style.width = "100%"

  override val dataDisplay: html.Element = document.createElement("div").asInstanceOf[html.Div]
  dataDisplay.appendChild(textDisplay)
  dataDisplay.appendChild(chartHolder)

  var currentData = js.Array[js.Array[Double]]()
  var nextIndex = 0

  override def updateDisplay(newData: String): Unit = {
    val parsedDisplay: Number = properties.numberType match {
      case "byte" => java.lang.Byte.parseByte(newData)
      case "double" => java.lang.Double.parseDouble(newData)
      case "float" => java.lang.Float.parseFloat(newData)
      case "integer" => java.lang.Integer.parseInt(newData)
      case "long" => java.lang.Long.parseLong(newData)
      case "short" => java.lang.Short.parseShort(newData)
    }

    if (currentData.length == 50) {
      currentData.shift()
    }

    currentData.push(js.Array(nextIndex, parsedDisplay.doubleValue()))
    nextIndex += 1

    jQuery.plot(
      chartHolder,
      js.Array(
        js.Dynamic.literal(
          data = currentData,
          lines = js.Dynamic.literal(
            show = true,
            fill = true
          )
        )
      )
    )

    textDisplay.innerHTML = parsedDisplay.toString
  }
}
