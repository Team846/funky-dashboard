package com.lynbrookrobotics.funkydashboard

import com.lynbrookrobotics.flot.Flot._
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB}
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom.html
import org.scalajs.jquery.jQuery

import scala.scalajs.js
import js.JSConverters._

object FlotLineChart {
  case class Props(newPoints: List[(Double, Double)])

  class Backend($: BackendScope[Props, Unit]) {
    def displayPlot = Callback {
      jQuery.plot(
        $.refs("chart-container").asInstanceOf[html.Div],
        js.Array(
          js.Dynamic.literal(
            data = $.props.runNow().newPoints.map(t => js.Array(t._1, t._2)).toJSArray,
            lines = js.Dynamic.literal(
              show = true,
              fill = true
            )
          )
        )
      )
    }

    def render(props: Props) = {
      // 16:9 aspect ratio assuming width of 576px (on full-screen Retina MBP)
      div(ref := "chart-container", height := "324px", width := "100%")
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .shouldComponentUpdate(_ => false)
    .componentDidMount(_.backend.displayPlot)
    .componentWillReceiveProps(_.component.backend.displayPlot)
    .build

  def apply(newPoints: List[(Double, Double)]) = {
    component(Props(newPoints))
  }
}
