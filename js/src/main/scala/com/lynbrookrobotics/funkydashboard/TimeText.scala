package com.lynbrookrobotics.funkydashboard

import slinky.core.Component
import slinky.core.annotations.react
import slinky.web.html._

import scala.collection.immutable.Queue

@react class TimeText extends Component {
  case class Props(newPoints: Queue[TimedValue[String]])
  type State = Unit

  override def initialState: Unit = ()

  def render = {
    div(
      if (props.newPoints.nonEmpty) {
        Some(div(
          p(props.newPoints.last.value)
        ))
      } else None
    )
  }
}
