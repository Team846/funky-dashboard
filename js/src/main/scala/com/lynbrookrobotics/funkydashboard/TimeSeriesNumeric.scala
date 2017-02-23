package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import japgolly.scalajs.react.vdom.all._

import scala.collection.immutable.Queue

object TimeSeriesNumeric {
  case class Props(newPoints: Seq[TimedValue[Double]])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      import props._

      div(
        if (newPoints.nonEmpty) {
          h3(textAlign := "center")(newPoints.last.value)
        } else EmptyTag,
        SlidingLineChart(newPoints)
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .build

  def apply(newPoints: Seq[TimedValue[Double]]) = {
    component(Props(newPoints))
  }
}
