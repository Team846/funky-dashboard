package com.lynbrookrobotics.funkydashboard

import com.lynbrookrobotics.chartjs.Chart
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB}
import org.scalajs.dom.{CanvasRenderingContext2D, html}
import scala.collection.immutable.Queue
import scala.scalajs.js

object MultipleSlidingLineChart {
  case class Props(points: Seq[TimedValue[Seq[Double]]])

  class Backend($: BackendScope[Props, Option[Chart]]) {
    def onMount = {
      val chart = new Chart(
        $.refs("chart-container").asInstanceOf[html.Canvas].getContext("2d").asInstanceOf[CanvasRenderingContext2D],
        js.Dynamic.literal(
          "type" -> "line",
          "data" -> js.Dynamic.literal(
            "datasets" -> js.Array(
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
            ), "legend" -> js.Dynamic.literal(
              "display" -> true
            )
          )
        )
      )

      val data = chart.data.datasets
      val colorList = List(
        "rgb(244, 66, 66)", "rgb(244, 223, 65)",
        "rgb(154, 244, 65)", "rgb(65, 244, 145)",
        "rgb(65, 244, 241)", "rgb(65, 88, 244)")

      for ((_, i) <- $.props.runNow().points.last.value.zipWithIndex) {
        data.push(js.Dynamic.literal(
          "label" -> s"Dataset $i",
          "borderColor" -> colorList(i),
          "fill" -> false,
          "data" -> js.Array()
        ))
      }

      drawChart(chart)

      $.setState(Some(chart))
    }

    var lastPoints = Seq.empty[TimedValue[Seq[Double]]]

    def drawChart(c: Chart): Unit = {
      val lastTimeStamp = if (lastPoints.isEmpty) -1 else lastPoints.last.time

      val points = $.props.runNow().points

      if (points.nonEmpty) {
        val newPoints = points.dropWhile(_.time <= lastTimeStamp)

        val numRemoved = if (newPoints.nonEmpty) {
          lastPoints.view.takeWhile(_.time < points.head.time).size
        } else 0

        lastPoints = points

        val data = c.data.datasets.asInstanceOf[js.Array[js.Dynamic]].map(t => t.data.asInstanceOf[js.Array[js.Dynamic]])

        newPoints.foreach { p =>
          for ((d, i) <- data.zipWithIndex) {
            d.push(js.Dynamic.literal(
              "x" -> p.time,
              "y" -> p.value(i)
            ))
          }
        }

        (1 to numRemoved).foreach { i =>
          data.foreach(t => t.shift())
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
    .shouldComponentUpdate(s => s.$.props != s.nextProps || s.$.state != s.nextState)
    .componentDidMount(_.backend.onMount)
    .componentDidUpdate(_.component.backend.onUpdate)
    .build

  def apply(newPoints: Seq[TimedValue[Seq[Double]]]) = {
    component(Props(newPoints))
  }
}