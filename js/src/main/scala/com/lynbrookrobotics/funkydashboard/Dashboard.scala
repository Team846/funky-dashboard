package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB}
import org.scalajs.dom.ext.Ajax
import com.payalabs.scalajs.react.mdl._
import org.scalajs.dom._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import upickle.default._

import scala.collection.immutable.Queue

object DashboardContainer {
  class Backend($: BackendScope[Unit, Vector[DatasetGroupDefinition]]) {
    def componentDidMount: Callback = Callback {
      Ajax.get("/datasets.json").foreach { result =>
        val text = result.responseText
        val parsed: Vector[DatasetGroupDefinition] = read[Vector[DatasetGroupDefinition]](text)
        $.setState(parsed).runNow()
      }
    }

    def render(state: Vector[DatasetGroupDefinition]) = {
      div(
        Dashboard(state)
      )
    }
  }

  val component = ReactComponentB[Unit](getClass.getSimpleName)
    .initialState(Vector.empty[DatasetGroupDefinition])
    .renderBackend[Backend]
    .componentDidMount(_.backend.componentDidMount)
    .build

  def apply() = component()
}

object Dashboard {
  case class Props(groups: Vector[DatasetGroupDefinition])
  case class State(postToServer: String => Unit, paused: Boolean, activeGroupIndex: Int, pointsWindow: Queue[(Double, Map[String, Map[String, String]])])

  class Backend($: BackendScope[Props, State]) {
    val websocketProtocol = if (window.location.protocol == "https") {
      "wss"
    } else {
      "ws"
    }

    def componentDidMount: Callback = Callback {
      val datastream = new WebSocket(s"$websocketProtocol://${window.location.host}/datastream")

      $.modState { state =>
        state.copy(
          postToServer = s => {
            println(s"posting $s")
            datastream.send(s)
          }
        )
      }.runNow()

      datastream.onmessage = (e: MessageEvent) => {
        val newValues = read[(Double, Map[String, Map[String, String]])](e.data.toString)
        $.modState { state =>
          state.copy(
            pointsWindow = (state.pointsWindow :+ newValues).takeRight(50)
          )
        }.runNow()
      }
    }

    def render(props: Props, state: State) = {
      import props._, state._

      div(className := "mdl-layout mdl-js-layout mdl-layout--fixed-drawer mdl-layout--fixed-header")(
        header(className := "mdl-layout__header")(
          div(className := "mdl-layout__header-row")(
            span(className := "mdl-layout-title")("Funky Dashboard"),
            div(className := "mdl-layout-spacer"),
            button(
              id := "pause-button",
              className := "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent",
              onClick --> $.modState(s => s.copy(paused = !s.paused))
            )("Toggle Pause").material
          )
        ),
        div(className := "mdl-layout__drawer mdl-color--blue-grey-900 mdl-color-text--blue-grey-50")(
          header(
            img(src := "/images/Team_846_Logo.png", className := "team-logo")
          ),
          nav(id := "group-chooser", className := "group-chooser mdl-navigation mdl-color--blue-grey-800")(
            groups.zipWithIndex.map { case (group, index) =>
              a(
                className := "mdl-navigation__link",
                backgroundColor := (if (index == activeGroupIndex) "#00ACC1" else "transparent"),
                cursor := "pointer",
                onClick --> $.modState(_.copy(activeGroupIndex = index))
              )(group.name)
            }
          )
        ),
        main(className := "mdl-layout__content mdl-color--grey-100", id := "groups-container")(
          div(className := "mdl-grid")(
            if (activeGroupIndex < groups.size) {
              val group = groups(activeGroupIndex)
              group.datasets.map { d =>
                DatasetCard(
                  d.name,
                  Dataset.extract(d, s => postToServer(write((group.name, d.name, s))))(pointsWindow.flatMap { case (timestamp, updates) =>
                    updates.get(group.name).flatMap(_.get(d.name)).map(v => (timestamp, v))
                  })
                )
              }
            } else {
              h1("Loading")
            }
          )
        )
      ).material
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .initialState(State(s => (), false, 0, Queue.empty[(Double, Map[String, Map[String, String]])]))
    .renderBackend[Backend]
    .componentDidMount(_.backend.componentDidMount)
    .shouldComponentUpdate(b => !b.$.state.paused || b.nextState.activeGroupIndex != b.$.state.activeGroupIndex)
    .build

  def apply(groups: Vector[DatasetGroupDefinition]) =
    component(Props(groups))
}
