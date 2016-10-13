package com.lynbrookrobotics.funkydashboard

import scala.scalajs.js
import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import japgolly.scalajs.react.vdom.all._

object TimeSeriesNumeric {
  case class Props(newPoints: List[(Double, TimeSeriesValue)])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      import props._

      val newValues = newPoints.map(t => (t._1, t._2.value))

      div(
        FlotLineChart(newValues)
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .build

  def apply(newPoints: List[(Double, TimeSeriesValue)]) = {
    component(Props(newPoints))
  }
}
