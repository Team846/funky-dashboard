package com.lynbrookrobotics.funkydashboard

import me.shadaj.slinky.core.Component
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.web.html._
import org.scalajs.dom
import org.scalajs.dom.raw.{Blob, BlobPropertyBag, URL}

import scala.scalajs.js
import com.lynbrookrobotics.mdl._

@react class TimeSeriesNumeric extends Component {
  case class Props(newPoints: Seq[TimedValue[Double]])
  type State = Option[Seq[TimedValue[Double]]]

  def handleRecordToggle(): Unit = {
    if (state.isDefined) {
      val firstTime = state.get.head.time
      val text = ("Time,Value" +: state.get.map { v =>
        s"${(v.time - firstTime).toDouble / 1000},${v.value}"
      }).mkString("\n")

      val blob = new Blob(js.Array(text: js.Any), BlobPropertyBag("text/csv"))
      val url = URL.createObjectURL(blob)
      dom.window.location.assign(url)
      setState(None)
    } else {
      setState(Some(Seq.empty))
    }
  }

  def initialState: State = None

  def render = {
    val isRecording = state

    div(
      if (props.newPoints.nonEmpty) {
        Some(h3(style := js.Dynamic.literal(textAlign = "center"))(
          "%.3f".format(props.newPoints.last.value)
        ))
      } else None,
      SlidingLineChart(props.newPoints, newPoints => {
        setState(state.map(s => s ++ newPoints))
      }),
      button(
        style := js.Dynamic.literal(
          marginLeft = 16
        ),
        className := "mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--accent",
        onClick := (_ => handleRecordToggle())
      )(if (isRecording.isDefined) "Stop Recording" else "Start recording").material
    )
  }
}
