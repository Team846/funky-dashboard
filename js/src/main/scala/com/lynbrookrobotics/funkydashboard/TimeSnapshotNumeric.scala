package com.lynbrookrobotics.funkydashboard

import slinky.core.Component
import slinky.core.annotations.react
import slinky.web.html._

import scala.collection.immutable.Queue
import scala.scalajs.js

@react class TimeSnapshotNumeric extends Component {
  case class Props(newPoints: Queue[TimedValue[Option[Double]]])
  type State = Unit

  override def initialState: Unit = ()

  def render = {
    val segment = props.newPoints.view.reverse.dropWhile(_.value.isEmpty)
      .takeWhile(_.value.isDefined).reverse.map(t => TimedValue(t.time, t.value.get))

    div(
      if (props.newPoints.nonEmpty) {
        Some(h3(style := js.Dynamic.literal(textAlign = "center"))("%.3f".format(segment.last.value)))
      } else None,
      SlidingLineChart(segment, _ => ())
    )
  }
}
