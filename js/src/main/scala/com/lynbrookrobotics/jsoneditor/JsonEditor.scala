package com.lynbrookrobotics.jsoneditor

import me.shadaj.slinky.core.ExternalComponent
import me.shadaj.slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("react-json-edit", JSImport.Namespace)
object JsonEditorModule extends js.Object {
  val JsonEditor: js.Object = js.native
}

@react object JsonEditor extends ExternalComponent {
  case class Props(value: js.UndefOr[js.Object],
                   propagateChanges: js.UndefOr[js.Object => Unit],
                   styling: js.UndefOr[js.Object])

  override val component = JsonEditorModule.JsonEditor
}
