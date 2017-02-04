package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB, ReactComponentU, TopNode}
import japgolly.scalajs.react.vdom.all._

import scala.collection.immutable.Queue

object ImageStream {
  case class Props(newPoints: Queue[ImageStreamValue])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      val images = $.props.runNow().newPoints
      val last = if (images.nonEmpty) images.last.value else ""

      div(
        img(src := s"data:image/png;base64,$last")
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .shouldComponentUpdate(_.nextProps.newPoints.nonEmpty)
    .build

  def apply(newPoints: Queue[ImageStreamValue]) = {
    component(Props(newPoints))
  }
}
