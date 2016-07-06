package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.{ReactComponentU, TopNode}

object Dataset {
  def extract(definition: DatasetDefinition): List[Any] => ReactComponentU[_, _, _, TopNode] = {
    definition.`type` match {
      case "time-series-numeric" =>
        values => TimeSeriesNumeric(
          definition.properties.asInstanceOf[TimeSeriesNumericProperties],
          values.map(_.asInstanceOf[String])
        )
      case "image-stream" =>
        values => ImageStream(
          values.map(_.asInstanceOf[String])
        )
    }
  }
}