package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}

import scala.collection.immutable.Queue

object MultipleTimeSeriesNumeric {
  case class Props(newPoints: Queue[(Double, TimeSeriesListValue)])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      import props._

      val newValues = newPoints.map(t => (t._1, t._2.value))

      div(
        if (newValues.nonEmpty) {
          h3(textAlign := "center")(newValues.last._2.last)
        } else EmptyTag,
        MultipleSlidingLineChart(newValues)
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .build

  def apply(newPoints: Queue[(Double, TimeSeriesListValue)]) = {
    component(Props(newPoints))
  }
}