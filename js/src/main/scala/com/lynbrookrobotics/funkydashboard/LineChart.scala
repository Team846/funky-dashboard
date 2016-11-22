package com.lynbrookrobotics.funkydashboard

import com.lynbrookrobotics.chartjs.Chart
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB}
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom.{CanvasRenderingContext2D, html}

import scala.scalajs.js
import js.JSConverters._

object LineChart {
  case class Props(newPoints: List[(Double, Double)])

  class Backend($: BackendScope[Props, Option[Chart]]) {
    def onMount = {
      val chart = new Chart(
        $.refs("chart-container").asInstanceOf[html.Canvas].getContext("2d").asInstanceOf[CanvasRenderingContext2D],
        js.Dynamic.literal(
          "type" -> "line",
          "data" -> js.Dynamic.literal(
            "datasets" -> js.Array(
              js.Dynamic.literal(
                "data" -> js.Array()
              )
            )
          ),
          "options" -> js.Dynamic.literal(
            "scales" -> js.Dynamic.literal(
              "xAxes" -> js.Array(
                js.Dynamic.literal(
                  "type" -> "time",
                  "position" -> "bottom"
                )
              )
            ),
            "animation" -> false,
            "legend" -> js.Dynamic.literal(
              "display" -> false
            )
          )
        )
      )

      drawChart(chart)

      $.setState(Some(chart))
    }

    def drawChart(c: Chart): Unit = {
      val newPoints = $.props.runNow().newPoints
      val newData = newPoints.map { p =>
        js.Dynamic.literal(
          "x" -> p._1,
          "y" -> p._2
        )
      }

      c.data.datasets.asInstanceOf[js.Array[js.Dynamic]](0).data = newData.toJSArray

      c.update()
    }

    def onUpdate = Callback {
      $.state.runNow().foreach { c =>
        drawChart(c)
      }
    }

    def render(props: Props) = {
      // 16:9 aspect ratio assuming width of 576px (on full-screen Retina MBP)
      canvas(ref := "chart-container")
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .initialState(None: Option[Chart])
    .renderBackend[Backend]
    .shouldComponentUpdate(_ => false)
    .componentDidMount(_.backend.onMount)
    .componentWillReceiveProps(_.component.backend.onUpdate)
    .build

  def apply(newPoints: List[(Double, Double)]) = {
    component(Props(newPoints))
  }
}
