package com.lynbrookrobotics.funkydashboard

import slinky.core.Component
import slinky.core.annotations.react
import slinky.web.html._
import org.scalajs.dom._
import com.lynbrookrobotics.mdl._
import org.scalajs.dom
import org.scalajs.dom.raw.{Blob, BlobPropertyBag, URL}

import scala.collection.immutable.Queue
import scala.scalajs.js

import argonaut._, Argonaut._, ArgonautShapeless._

@react
class Dashboard extends Component {

  case class Props()

  case class State(postToServer: Option[String => Unit],
                   paused: Boolean, activeGroupIndex: Int,
                   isRecording: Boolean,
                   groups: Vector[DatasetGroupDefinition],
                   pointsWindow: Queue[TimedValue[Map[String, Map[String, String]]]])

  override def initialState: State = State(
    None,
    false,
    0,
    false,
    Vector.empty,
    Queue.empty[TimedValue[Map[String, Map[String, String]]]]
  )

  override def shouldComponentUpdate(nextProps: Props, nextState: State): Boolean = {
    !state.paused || nextState.activeGroupIndex != state.activeGroupIndex
  }

  def connectWebsocket(): Unit = {
    println(s"Attempting to connect to server")
    val datastream = new EventSource("/sse")

    datastream.onerror = _ => {
      setState(initialState)
    }

    datastream.onmessage = (e: MessageEvent) => {
      if (state.postToServer.isDefined) {
        val newValues = e.data.asInstanceOf[String].decodeOption[TimedValue[Map[String, Map[String, String]]]].get
        setState(state.copy(
          pointsWindow = (state.pointsWindow :+ newValues).takeRight(50)
        ))
      } else {
        setState(state.copy(
          postToServer = Some(d => {
            val req = new XMLHttpRequest
            req.open("POST", "/post")
            req.send(d)
          }),
          groups = e.data.asInstanceOf[String].decodeOption[Vector[DatasetGroupDefinition]].get
        ))
      }
    }
  }

  override def componentDidMount() = {
    connectWebsocket()
  }

  var firstTime: Long = 0

  var data: String = ""

  def toggleRecording() = {
    if (!state.isRecording) {
      firstTime = state.pointsWindow.last.time
      data = s"Time,${state.pointsWindow.last.value.values.flatMap(_.keys).mkString(",")}\n"
      setState(_.copy(isRecording = true))
    } else {
      setState(_.copy(isRecording = false))
      val blob = new Blob(js.Array(data: js.Any), BlobPropertyBag("text/csv"))
      val url = URL.createObjectURL(blob)
      dom.window.location.assign(url)
    }
  }

  override def componentDidUpdate(prevProps: Props, prevState: State): Unit = {
    if (state.isRecording) {
      val timeColumn = s"${(state.pointsWindow.last.time - firstTime).toDouble / 1000}"

      val cardValues = state.pointsWindow.last.value.flatMap(x => x._2.map(y => {
        var string = y._2

        //deal with commas by appending quotes around string, but only if needed
        if (string.head == '\"') string = string.tail
        if (string.last ==  '\"') string = string.init

        //deal with double quotes inside string
        "\"" + string.replace("\"", "\"\"") + "\""
      })).mkString(",")

      //add new line
      data += s"$timeColumn,$cardValues\n"
    }
  }

  def render = {
    val State(postToServer, paused, activeGroupIndex, isRecording, groups, pointsWindow) = state

    div(className := "mdl-layout mdl-js-layout mdl-layout--fixed-drawer mdl-layout--fixed-header")(
      header(className := "mdl-layout__header")(
        div(className := "mdl-layout__header-row")(
          span(className := "mdl-layout-title")("Funky Dashboard"),
          div(className := "mdl-layout-spacer"),
          button(
            style := js.Dynamic.literal(
              marginRight = "15px"
            ),
            className := "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent",
            onClick := (_ => setState(state.copy(paused = !state.paused)))
          )(if (paused) "Unpause" else "Pause").material,
          button(
            className := "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent",
            onClick := (_ => toggleRecording())
          )(if (isRecording) "Stop Recording" else "Record Data").material
        )
      ),
      div(className := "mdl-layout__drawer mdl-color--blue-grey-900 mdl-color-text--blue-grey-50")(
        header(
          img(src := "/Team_846_Logo.png", className := "team-logo")
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
                  postToServer.get.apply(List(group.name, d.name, s).jencode.toString)
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
