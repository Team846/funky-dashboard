package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB, ReactComponentU, TopNode}
import japgolly.scalajs.react.vdom.all._

import scala.collection.immutable.Queue

object ImageStream {
  case class Props(newPoints: Queue[String])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      val images = $.props.runNow().newPoints
      val last = if (images.nonEmpty) images.last else ""

      div(
        img(width := "100%")(src := s"data:image/jpg;base64,$last")
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .shouldComponentUpdate(_.nextProps.newPoints.nonEmpty)
    .build

  def apply(newPoints: Queue[String]) = {
    component(Props(newPoints))
  }
}
