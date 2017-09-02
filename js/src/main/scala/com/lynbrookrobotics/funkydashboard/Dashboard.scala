package com.lynbrookrobotics.funkydashboard

import org.scalajs.dom.ext.Ajax
import me.shadaj.slinky.core.Component
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.web.html._
import org.scalajs.dom._
import play.api.libs.json.Json
import com.lynbrookrobotics.mdl._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.collection.immutable.Queue
import scala.scalajs.js

@react
class DashboardContainer extends Component {
  case class Props()
  type State = Vector[DatasetGroupDefinition]

  override def initialState: Vector[DatasetGroupDefinition] = Vector.empty

  override def componentDidMount: Unit = {
    Ajax.get("/datasets.json").foreach { result =>
      val text = result.responseText
      val parsed: Vector[DatasetGroupDefinition] = Json.parse(text).as[Vector[DatasetGroupDefinition]]
      setState(parsed)
    }
  }

  def render = {
    div(
      Dashboard(state)
    )
  }
}

import Dashboard.{Props, State} // patch for IntelliJ issues

@react
class Dashboard extends Component {
  case class Props(groups: Vector[DatasetGroupDefinition])
  case class State(postToServer: String => Unit, paused: Boolean, activeGroupIndex: Int, pointsWindow: Queue[TimedValue[Map[String, Map[String, String]]]])

  private val websocketProtocol = if (window.location.protocol == "https") {
    "wss"
  } else {
    "ws"
  }

  override def initialState: State = State(s => (), false, 0, Queue.empty[TimedValue[Map[String, Map[String, String]]]])

  override def shouldComponentUpdate(nextProps: Props, nextState: State): Boolean = {
    !state.paused || nextState.activeGroupIndex != state.activeGroupIndex
  }

  override def componentDidMount() = {
    val datastream = new WebSocket(s"$websocketProtocol://${window.location.host}/datastream")

    setState(state.copy(
      postToServer = datastream.send
    ))

    datastream.onmessage = (e: MessageEvent) => {
      val newValues = Json.parse(e.data.toString).as[TimedValue[Map[String, Map[String, String]]]]
      setState(state.copy(
        pointsWindow = (state.pointsWindow :+ newValues).takeRight(50)
      ))
    }
  }

  def render = {
    val Props(groups) = props
    val State(postToServer, _, activeGroupIndex, pointsWindow) = state

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
          if (activeGroupIndex < groups.size) {
            val group = groups(activeGroupIndex)
            group.datasets.zipWithIndex.map { case (d, i) =>
              DatasetCard(
                d.name,
                Dataset.extract(d, s => {
                  postToServer(Json.toJson(List(group.name, d.name, s)).toString)
                })(pointsWindow.flatMap { case TimedValue(timestamp, updates) =>
                  updates.get(group.name).flatMap(_.get(d.name)).map(v => TimedValue(timestamp, v))
                })
              ).apply(key = s"$activeGroupIndex-$i")
            }
          } else {
            h1("Loading")
          }
        )
      )
    ).material
  }
}
