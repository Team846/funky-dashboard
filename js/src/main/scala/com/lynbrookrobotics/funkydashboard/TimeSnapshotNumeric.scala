package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}

import scala.collection.immutable.Queue

object TimeSnapshotNumeric {
  case class Props(newPoints: Queue[TimedValue[Option[Double]]])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      import props._

      val segment = newPoints.view.reverse.dropWhile(_.value.isEmpty)
        .takeWhile(_.value.isDefined).reverse.map(t => TimedValue(t.time, t.value.get))

      div(
        if (newPoints.nonEmpty) {
          h3(textAlign := "center")(segment.last.value)
        } else EmptyTag,
        SlidingLineChart(segment)
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .build

  def apply(newPoints: Queue[TimedValue[Option[Double]]]) = {
    component(Props(newPoints))
  }
}
