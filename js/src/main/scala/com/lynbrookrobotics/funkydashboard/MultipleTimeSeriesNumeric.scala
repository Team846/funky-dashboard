package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}

import scala.collection.immutable.Queue

object MultipleTimeSeriesNumeric {
  case class Props(newPoints: Queue[TimedValue[Seq[Double]]])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      import props._

      div(
        if (newPoints.nonEmpty) {
          h3(textAlign := "center")(newPoints.last.value.mkString(","))
        } else EmptyTag,
        MultipleSlidingLineChart(newPoints)
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .build

  def apply(newPoints: Queue[TimedValue[Seq[Double]]]) = {
    component(Props(newPoints))
  }
}