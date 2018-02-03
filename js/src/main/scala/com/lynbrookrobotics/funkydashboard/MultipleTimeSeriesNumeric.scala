package com.lynbrookrobotics.funkydashboard

import slinky.core.Component
import slinky.core.annotations.react
import slinky.web.html._

import scala.collection.immutable.Queue
import scala.scalajs.js

@react class MultipleTimeSeriesNumeric extends Component {
  case class Props(newPoints: Queue[TimedValue[Seq[Double]]])
  type State = Unit

  override def initialState: Unit = ()

  def render = {
    div(
      if (props.newPoints.nonEmpty) {
        Some(h3(style := js.Dynamic.literal(textAlign = "center"))(props.newPoints.last.value.map("%.3f".format(_)).mkString(", ")))
      } else None,
      MultipleSlidingLineChart(props.newPoints)
    )
  }
}