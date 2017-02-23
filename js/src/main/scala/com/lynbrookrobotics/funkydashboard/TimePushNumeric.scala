package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}

import scala.collection.immutable.Queue

object TimePushNumeric {
  case class Props(newPoints: Queue[(Long, Seq[TimedValue[Double]])])

  class Backend($: BackendScope[Props, Unit]) {
    def render(props: Props) = {
      import props._

      TimeSeriesNumeric(
        newPoints.lastOption.map(l => l._2).getOrElse(Seq.empty)
      )
    }
  }

  val component = ReactComponentB[Props](getClass.getSimpleName)
    .stateless
    .renderBackend[Backend]
    .shouldComponentUpdate(s => s.$.props != s.nextProps)
    .build

  def apply(newPoints: Queue[(Long, Seq[TimedValue[Double]])]) = {
    component(Props(newPoints))
  }
}
