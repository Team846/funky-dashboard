package com.lynbrookrobotics.funkydashboard

import japgolly.scalajs.react.ReactDOM
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.JSApp

import com.lynbrookrobotics.{react => r}

object DashboardClient extends JSApp {
  def main(): Unit = {
    js.Dynamic.global.React    = r.React
    js.Dynamic.global.ReactDOM = r.ReactDOM

    ReactDOM.render(DashboardContainer(), dom.document.getElementById("main-container"))
  }
}
