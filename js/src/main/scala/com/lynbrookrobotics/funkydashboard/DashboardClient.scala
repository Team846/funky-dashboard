package com.lynbrookrobotics.funkydashboard

import me.shadaj.slinky.web.ReactDOM
import org.scalajs.dom

import scala.scalajs.js.JSApp

object DashboardClient extends JSApp {
  def main(): Unit = {
    ReactDOM.render(DashboardContainer(), dom.document.getElementById("main-container"))
  }
}
