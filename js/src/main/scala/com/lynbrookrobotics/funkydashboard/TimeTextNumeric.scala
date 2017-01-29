package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}

object TimeTextNumeric {
  case class Props(newPoints: List[(Double, TimeTextValue)])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      import props._

      div(
        if (newPoints.nonEmpty) {
          div(
            p(newPoints.last._2.message)
          )
        } else EmptyTag
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .build

  def apply(newPoints: List[(Double, TimeTextValue)]) = {
    component(Props(newPoints))
  }
}
