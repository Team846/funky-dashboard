package com.lynbrookrobotics.funkydashboard

import me.shadaj.slinky.core.Component
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.web.html._
import org.scalajs.dom._
import play.api.libs.json.Json
import com.lynbrookrobotics.mdl._
import org.scalajs.dom

import scala.collection.immutable.Queue
import scala.scalajs.js

@react
class Dashboard extends Component {
  case class Props()
  case class State(postToServer: Option[String => Unit],
                   paused: Boolean, activeGroupIndex: Int,
                   groups: Vector[DatasetGroupDefinition],
                   pointsWindow: Queue[TimedValue[Map[String, Map[String, String]]]])

  private val websocketProtocol = if (window.location.protocol == "https") {
    "wss"
  } else {
    "ws"
  }

  override def initialState: State = State(None, false, 0, Vector.empty, Queue.empty[TimedValue[Map[String, Map[String, String]]]])

  override def shouldComponentUpdate(nextProps: Props, nextState: State): Boolean = {
    !state.paused || nextState.activeGroupIndex != state.activeGroupIndex
  }

  def connectWebsocket(): Unit = {
    println(s"Attempting to connect websocket")
    val datastream = new WebSocket(s"$websocketProtocol://${window.location.host}/datastream")

    var hasRetried = false

    datastream.onclose = _ => {
      if (!hasRetried) {
        hasRetried = true
        setState(initialState)
        dom.window.setTimeout(connectWebsocket: () => Unit, 500)
      }
    }

    datastream.onerror = _ => {
      if (!hasRetried) {
        hasRetried = true
        setState(initialState)
        dom.window.setTimeout(connectWebsocket: () => Unit, 500)
      }
    }

    datastream.onmessage = (e: MessageEvent) => {
      if (state.postToServer.isDefined) {
        val newValues = Json.parse(e.data.toString).as[TimedValue[Map[String, Map[String, String]]]]
        setState(state.copy(
          pointsWindow = (state.pointsWindow :+ newValues).takeRight(50)
        ))
      } else {
        setState(state.copy(
          postToServer = Some(datastream.send),
          groups = Json.parse(e.data.toString).as[Vector[DatasetGroupDefinition]]
        ))
      }
    }
  }

  override def componentDidMount() = {
    connectWebsocket()
  }

  def render = {
    val State(postToServer, paused, activeGroupIndex, groups, pointsWindow) = state

    div(className := "mdl-layout mdl-js-layout mdl-layout--fixed-drawer mdl-layout--fixed-header")(
      header(className := "mdl-layout__header")(
        div(className := "mdl-layout__header-row")(
          span(className := "mdl-layout-title")("Funky Dashboard"),
          div(className := "mdl-layout-spacer"),
          button(
            id := "pause-button",
            className := "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent",
            onClick := (_ => setState(state.copy(paused = !state.paused)))
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
              style := js.Dynamic.literal(
                "backgroundColor" -> (if (index == activeGroupIndex) "#00ACC1" else "transparent"),
                "cursor" -> "pointer"
              ),
              onClick := (_ => setState(state.copy(activeGroupIndex = index)))
            )(group.name)
          }
        )
      ),
      main(className := "mdl-layout__content mdl-color--grey-100", id := "groups-container")(
        div(className := "mdl-grid")(
          if (postToServer.isDefined) {
            val group = groups(activeGroupIndex)
            group.datasets.zipWithIndex.map { case (d, i) =>
              DatasetCard(
                d.name,
                Dataset.extract(d, s => {
                  postToServer.get.apply(Json.toJson(List(group.name, d.name, s)).toString)
                })(pointsWindow.flatMap { case TimedValue(timestamp, updates) =>
                  updates.get(group.name).flatMap(_.get(d.name)).map(v => TimedValue(timestamp, v))
                })
              ).withKey(s"$activeGroupIndex-$i")
            }
          } else {
            h1("Loading")
          }
        )
      )
    ).material
  }
}
