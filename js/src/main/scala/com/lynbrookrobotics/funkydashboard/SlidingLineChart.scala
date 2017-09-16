package com.lynbrookrobotics.funkydashboard

import com.lynbrookrobotics.chartjs.Chart
import me.shadaj.slinky.core.Component
import me.shadaj.slinky.core.annotations.react
import me.shadaj.slinky.web.html._
import org.scalajs.dom.{CanvasRenderingContext2D, Element, html}

import scala.scalajs.js

@react class SlidingLineChart extends Component {
  case class Props(points: Seq[TimedValue[Double]], withNewPoints: Seq[TimedValue[Double]] => Unit)
  type State = Option[Chart]

  override def shouldComponentUpdate(nextProps: Props, nextState: Option[Chart]): Boolean = {
    props != nextProps || state != nextState
  }

  override def initialState: Option[Chart] = None

  var chartElem: Element = null

  override def componentDidMount(): Unit = {
    val chart = new Chart(
      chartElem.asInstanceOf[html.Canvas].getContext("2d").asInstanceOf[CanvasRenderingContext2D],
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

    setState(Some(chart))
  }

  var lastPoints = Seq.empty[TimedValue[Double]]

  def drawChart(c: Chart): Unit = {
    val lastTimeStamp = if (lastPoints.isEmpty) -1 else lastPoints.last.time

    val points = props.points

    if (points.nonEmpty) {
      val newPoints = points.dropWhile(_.time <= lastTimeStamp)

      if (newPoints.nonEmpty) {
        props.withNewPoints(newPoints)
      }

      val numRemoved = if (newPoints.nonEmpty) {
        lastPoints.view.takeWhile(_.time < points.head.time).size
      } else 0

      lastPoints = points

      val data = c.data.datasets.asInstanceOf[js.Array[js.Dynamic]](0)
        .data.asInstanceOf[js.Array[js.Dynamic]]

      newPoints.foreach { p =>
        data.push(js.Dynamic.literal(
          "x" -> p.time,
          "y" -> p.value
        ))
      }

      (1 to numRemoved).foreach { i =>
        data.shift()
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
