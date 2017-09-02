package com.lynbrookrobotics.funkydashboard

import me.shadaj.slinky.core.Component
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.web.html._

import scala.collection.immutable.Queue
import scala.scalajs.js

@react class TimeSeriesNumeric extends Component {
  case class Props(newPoints: Seq[TimedValue[Double]])
  type State = Unit

  def initialState: Unit = ()

  def render = {
    div(
      if (props.newPoints.nonEmpty) {
        Some(h3(style := js.Dynamic.literal(textAlign = "center"))(
          props.newPoints.last.value.toString
        ))
      } else None,
      SlidingLineChart(props.newPoints)
    )
  }
}
