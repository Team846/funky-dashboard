package com.lynbrookrobotics.funkydashboard

import scala.scalajs.js
import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import japgolly.scalajs.react.vdom.all._

trait TimeSeriesNumericProperties extends js.Object {
  val numberType: String = js.native
}

object TimeSeriesNumeric {
  case class Props(properties: TimeSeriesNumericProperties, newPoints: List[String])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      import props._

      val newValues = newPoints.map(_.toDouble)

      div(
        FlotLineChart(newValues)
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .build

  def apply(properties: TimeSeriesNumericProperties, newPoints: List[String]) = {
    component(Props(properties, newPoints))
  }
}
