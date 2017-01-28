package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}

object TimeTextNumeric {
  case class Props(newPoints: List[(Double, TimeTextValue)])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      import props._

      val newValues = newPoints.map(t => (t._1, t._2.value))

      div(
        if (newValues.nonEmpty) {
          div(
            h3(textAlign := "center")(newValues.last._2))
          div(h3(textAlign := "center")(newPoints.last._2.message)
          )
        } else EmptyTag,
        SlidingLineChart(newValues)
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
