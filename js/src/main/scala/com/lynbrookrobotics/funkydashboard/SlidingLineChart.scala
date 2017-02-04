package com.lynbrookrobotics.funkydashboard

import com.lynbrookrobotics.chartjs.Chart
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB}
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom.{CanvasRenderingContext2D, html}

import scala.collection.immutable.Queue
import scala.scalajs.js

object SlidingLineChart {
  case class Props(points: Queue[(Double, Double)])

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
                  "time" -> js.Dynamic.literal(
                    "unitStepSize" -> 1000,
                    "unit" -> "millisecond",
                    "tooltipFormat" -> "SSS [ms]"
                  ),
                  "position" -> "bottom"
                )
              )
            ),
            "legend" -> js.Dynamic.literal(
              "display" -> false
            )
          )
        )
      )

      drawChart(chart)

      $.setState(Some(chart))
    }

    var lastPoints = Queue.empty[(Double, Double)]

    def drawChart(c: Chart): Unit = {
      val lastTimeStamp = if (lastPoints.isEmpty) -1 else lastPoints.last._1

      val points = $.props.runNow().points

      if (points.nonEmpty) {
        val newPoints = points.dropWhile(_._1 <= lastTimeStamp)

        val numRemoved = if (newPoints.nonEmpty) lastPoints.takeWhile(_._1 < points.head._1).size else 0

        lastPoints = points

        val data = c.data.datasets.asInstanceOf[js.Array[js.Dynamic]](0).data.asInstanceOf[js.Array[js.Dynamic]]

        newPoints.foreach { p =>
          data.push(js.Dynamic.literal(
            "x" -> p._1,
            "y" -> p._2
          ))
        }

        (1 to numRemoved).foreach { i =>
          data.shift()
        }

        c.update()
      }
    }

    def onUpdate = Callback {
      $.state.runNow().foreach { c =>
        drawChart(c)
      }
    }

    def render(props: Props) = canvas(ref := "chart-container")
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .initialState(None: Option[Chart])
    .renderBackend[Backend]
    .shouldComponentUpdate(_ => false)
    .componentDidMount(_.backend.onMount)
    .componentWillReceiveProps(_.component.backend.onUpdate)
    .build

  def apply(newPoints: Queue[(Double, Double)]) = {
    component(Props(newPoints))
  }
}
