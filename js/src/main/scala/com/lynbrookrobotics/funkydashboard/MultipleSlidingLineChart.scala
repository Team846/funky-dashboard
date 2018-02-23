package com.lynbrookrobotics.funkydashboard

import com.lynbrookrobotics.chartjs.Chart
import slinky.core.Component
import slinky.core.annotations.react
import slinky.web.html._
import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, Element, html}

import scala.scalajs.js

@react class MultipleSlidingLineChart extends Component {
  case class Props(points: Seq[TimedValue[Seq[Double]]])
  type State = Option[Chart]

  override def initialState: Option[Chart] = None

  override def shouldComponentUpdate(nextProps: Props, nextState: Option[Chart]): Boolean = {
    props != nextProps || state != nextState
  }

  var chartElem: Element = null

  override def componentDidMount(): Unit = {
    val chart = new Chart(
      chartElem.asInstanceOf[html.Canvas].getContext("2d").asInstanceOf[CanvasRenderingContext2D],
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
                  "displayFormats" -> js.Dynamic.literal(
                    "millisecond" -> "ss.SSS"
                  ),
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

    props.points.last.value.indices.foreach { i =>
      data.push(js.Dynamic.literal(
        "label" -> s"Dataset $i",
        "borderColor" -> colorList(i),
        "fill" -> false,
        "data" -> js.Array()
      ))
    }

    drawChart(chart)

    setState(Some(chart))
  }

  var lastPoints = Seq.empty[TimedValue[Seq[Double]]]

  def drawChart(c: Chart): Unit = {
    val lastTimeStamp = if (lastPoints.isEmpty) -1 else lastPoints.last.time

    val points = props.points

    if (points.nonEmpty) {
      val newPoints = points.dropWhile(_.time <= lastTimeStamp)

      val numRemoved = if (newPoints.nonEmpty) {
        lastPoints.view.takeWhile(_.time < points.head.time).size
      } else 0

      lastPoints = points

      val data = c.data.datasets.asInstanceOf[js.Array[js.Dynamic]].map(t => t.data.asInstanceOf[js.Array[js.Dynamic]])

      newPoints.foreach { p =>
        data.zipWithIndex.foreach { case (d, i) =>
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

  override def componentDidUpdate(prevProps: Props, prevState: Option[Chart]): Unit = {
    state.foreach { c =>
      drawChart(c)
    }
  }

  def render = canvas(ref := (chartElem = _))
}
