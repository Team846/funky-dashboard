package com.lynbrookrobotics.funkydashboard

import slinky.core.Component
import slinky.core.annotations.react
import slinky.core.facade.ReactElement
import slinky.web.html._

import scala.scalajs.js

@react class DatasetCard extends Component {
  case class Props(name: String, child: ReactElement)
  type State = Unit

  override def initialState: State = ()

  def render = {
    div(
      key := props.name,
      className := "mdl-card mdl-shadow--16dp mdl-cell mdl-cell--6-col",
      style := js.Dynamic.literal("minHeight" -> 0)
    )(
      props.child,
      div(className := "mdl-card__title")(props.name)
    )
  }
}
