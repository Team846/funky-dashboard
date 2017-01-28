package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}

object TimeSnapshotNumeric {
  case class Props(newPoints: List[(Double, TimeSnapshotValue)])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      import props._

      val newValues = newPoints.map(t => (t._1, t._2.value))

      div(
        if (newValues.nonEmpty) {
          h3(textAlign := "center")(newValues.last._2)
        } else EmptyTag,
        SlidingLineChart(newValues.dropWhile(_._2.isEmpty)
          .takeWhile(_._2.isDefined).map(t => t._1 -> t._2.get))
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .build

  def apply(newPoints: List[(Double, TimeSnapshotValue)]) = {
    val reverseList = newPoints.reverse.dropWhile(_._2.value.isEmpty)
    val newList = reverseList.takeWhile(_._2.value.isDefined)
    component(Props(newList.reverse))
  }
}
