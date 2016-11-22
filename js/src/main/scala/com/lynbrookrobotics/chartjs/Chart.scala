package com.lynbrookrobotics.chartjs

import org.scalajs.dom._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("chart.js", "Chart")
@js.native
class Chart(ctx: CanvasRenderingContext2D, setup: js.Dynamic) extends js.Object {
  val data: js.Dynamic = js.native

  def update(): Unit = js.native
}

