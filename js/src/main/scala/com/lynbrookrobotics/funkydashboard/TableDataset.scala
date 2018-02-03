package com.lynbrookrobotics.funkydashboard

import slinky.core.Component
import slinky.core.annotations.react
import slinky.web.html._

import scala.collection.immutable.Queue
import scala.scalajs.js

@react class TableDataset extends Component {
  case class Props(newPoints: Queue[TimedValue[List[TablePair]]])
  type State = Unit

  override def initialState: Unit = ()

  def render = {
    table(
      className := "mdl-data-table mdl-js-data-table mdl-shadow--2dp",
      style := js.Dynamic.literal(width = "100%")
    )(
      thead(
        tr(th("Key"), th("Value"))
      ),
      tbody(
        props.newPoints.last.value.map { elem =>
          tr(
            td(elem.key),
            td(elem.value)
          )
        }
      )
    )
  }
}