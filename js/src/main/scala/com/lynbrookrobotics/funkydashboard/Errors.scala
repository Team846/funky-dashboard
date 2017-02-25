package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}

import scala.collection.immutable.Queue

object Errors {

  case class Props(newPoints: Queue[String])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      div(
        p("errors are toasted")
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .build

  def apply(newPoints: Queue[String]) = {
    component(Props(newPoints))
  }
}