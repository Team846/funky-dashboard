package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.ReactDOM
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.JSApp

object DashboardClient extends JSApp {
  def main(): Unit = {
    js.Dynamic.global.JsonEditor = js.Dynamic.global.ReactJsonEditor.JsonEditor
    ReactDOM.render(DashboardContainer(), dom.document.getElementById("main-container"))
  }
}
