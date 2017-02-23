package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}

import scala.collection.immutable.Queue

object TimeText {
  case class Props(newPoints: Queue[TimedValue[String]])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      import props._

      div(
        if (newPoints.nonEmpty) {
          div(
            p(newPoints.last.value)
          )
        } else EmptyTag
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .build

  def apply(newPoints: Queue[TimedValue[String]]) = {
    component(Props(newPoints))
  }
}
