package com.lynbrookrobotics.funkydashboard

import com.lynbrookrobotics.jsoneditor.JsonEditor
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}

import scala.collection.immutable.Queue
import scala.scalajs.js
import scala.scalajs.js.JSON

object JsonEditorDataset {
  case class Props(newPoints: Queue[(Double, JsonEditorValue)], sendData: String => Unit)

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      import props._

      div(
        if (newPoints.nonEmpty) {
          div(margin := 10)(
            JsonEditor(
              value = JSON.parse(newPoints.last._2.json),
              propagateChanges = (d: js.Dynamic) => {
                sendData(JSON.stringify(d))
              },
              styling = js.Dynamic.literal(
                "object" -> js.Dynamic.literal(
                  "display" -> "inline"
                ),
                "object-row" -> js.Dynamic.literal(
                  "marginLeft" -> 15
                ),
                "add-button" -> js.Dynamic.literal(
                  "display" -> "block",
                  "marginLeft" -> 15
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
            )()
          )
        } else EmptyTag
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .build

  def apply(newPoints: Queue[(Double, JsonEditorValue)], sendData: String => Unit) = {
    component(Props(newPoints, sendData))
  }
}