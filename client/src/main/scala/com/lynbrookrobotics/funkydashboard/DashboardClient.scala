package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.ReactDOM
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax

import scala.scalajs.js
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.{JSApp, JSON}
import org.scalajs.dom._
import org.scalajs.jquery._

object DashboardClient extends JSApp {
  def main(): Unit = {
    ReactDOM.render(DashboardContainer(), dom.document.getElementById("main-container"))
  }
}
