package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, ReactComponentB, ReactElement}

object DatasetCard {
  case class Props(name: String, child: ReactElement)

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      div(
        key := props.name,
        className := "mdl-card mdl-shadow--16dp mdl-cell mdl-cell--6-col",
        minHeight := "0px"
      )(
        props.child,
        div(className := "mdl-card__title")(props.name)
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .build

  def apply(name: String, child: ReactElement) = {
    component(Props(name, child))
  }
}
