package com.lynbrookrobotics.funkydashboard

import scala.scalajs.js
import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import japgolly.scalajs.react.vdom.all._

import scala.collection.immutable.Queue

object TimeSeriesNumeric {
  case class Props(newPoints: Queue[(Double, TimeSeriesValue)])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      import props._

      val newValues = newPoints.map(t => (t._1, t._2.value))

      div(
        if (newValues.nonEmpty) {
          h3(textAlign := "center")(newValues.last._2)
        } else EmptyTag,
        SlidingLineChart(newValues)
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .build

  def apply(newPoints: Queue[(Double, TimeSeriesValue)]) = {
    component(Props(newPoints))
  }
}
