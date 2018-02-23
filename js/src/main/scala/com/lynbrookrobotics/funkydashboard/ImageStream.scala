package com.lynbrookrobotics.funkydashboard

import slinky.core.Component
import slinky.core.annotations.react
import slinky.web.html._

import scala.collection.immutable.Queue
import scala.scalajs.js

@react class ImageStream extends Component {
  case class Props(newPoints: Queue[String])
  type State = Unit

  override def initialState: Unit = ()

  override def shouldComponentUpdate(nextProps: Props, nextState: Unit): Boolean = {
    nextProps.newPoints.nonEmpty
  }

  def render = {
    val images = props.newPoints
    val last = if (images.nonEmpty) images.last else ""

    div(
      img(style := js.Dynamic.literal(width = "100%"), src := s"data:image/jpg;base64,$last")
    )
  }
}
