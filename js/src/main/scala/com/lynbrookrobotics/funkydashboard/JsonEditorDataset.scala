package com.lynbrookrobotics.funkydashboard

import com.lynbrookrobotics.jsoneditor.JsonEditor
import me.shadaj.slinky.core.Component
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.web.html._

import scala.collection.immutable.Queue
import scala.scalajs.js
import scala.scalajs.js.JSON

@react class JsonEditorDataset extends Component {
  case class Props(newPoints: Queue[TimedValue[String]], sendData: String => Unit)
  type State = Unit

  override def initialState: Unit = ()

  override def shouldComponentUpdate(nextProps: Props, nextState: Unit): Boolean = {
    nextProps.newPoints.lastOption != props.newPoints.lastOption
  }

  def render = {
    div(
      if (props.newPoints.nonEmpty) {
        div(style := js.Dynamic.literal(margin = 10))(
          Some(JsonEditor(
            JSON.parse(props.newPoints.last.value).asInstanceOf[js.Object],
            (d: js.Object) => {
              props.sendData(JSON.stringify(d))
            },
            js.Dynamic.literal(
              "object" -> js.Dynamic.literal(
                "display" -> "inline"
              ),
              "array" -> js.Dynamic.literal(
                "display" -> "inline"
              ),
              "object-row" -> js.Dynamic.literal(
                "marginLeft" -> 15
              ),
              "array-row" -> js.Dynamic.literal(
                "marginLeft" -> 15
              ),
              "add-button" -> js.Dynamic.literal(
                "display" -> "none"
              ),
              "add-group" -> js.Dynamic.literal(
                "marginLeft" -> 15
              ),
              "add-input" -> js.Dynamic.literal(
                "display" -> "inline"
              ),
              "delete-button" -> js.Dynamic.literal(
                "display" -> "none"
              ),
              "key" -> js.Dynamic.literal(
                "display" -> "inline",
                "fontWeight" -> "bold",
                "marginRight" -> 5
              )
            )
          ))
        )
      } else None
    )
  }
}
