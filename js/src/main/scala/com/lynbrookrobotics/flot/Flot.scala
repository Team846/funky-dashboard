package com.lynbrookrobotics.flot

import org.scalajs.jquery._
import org.scalajs.dom._

import scala.scalajs.js

trait Flot extends JQueryStatic {
  // see http://www.flotcharts.org/flot/examples/series-types/index.html for examples
  def plot(placeholder: html.Element, data: js.Array[js.Dynamic]): Unit = js.native
}

object Flot {
  implicit def jq2Flot(jq: JQueryStatic): Flot = jq.asInstanceOf[Flot]
}