package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB, ReactComponentU, TopNode}
import japgolly.scalajs.react.vdom.all._

object ImageStream {
  case class Props(newPoints: List[String])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      val images = $.props.runNow().newPoints
      val last = if (images.nonEmpty) images.last else ""

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

  def apply(newPoints: List[String]) = {
    component(Props(newPoints))
  }
}
