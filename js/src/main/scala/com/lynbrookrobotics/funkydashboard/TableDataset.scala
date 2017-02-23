package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}

import scala.collection.immutable.Queue

object TableDataset {
  case class Props(newPoints: Queue[TimedValue[List[TablePair]]])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      import props._

      table(className := "mdl-data-table mdl-js-data-table mdl-shadow--2dp", width := "100%")(
        thead(
          tr(th("Key"), th("Value"))
        ),
        tbody(
          newPoints.last.value.map { elem =>
            tr(
              td(elem.key),
              td(elem.value)
            )
          }
        )
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .build

  def apply(newPoints: Queue[TimedValue[List[TablePair]]]) = {
    component(Props(newPoints))
  }

}