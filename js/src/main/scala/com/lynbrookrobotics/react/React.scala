package com.lynbrookrobotics.react

import japgolly.scalajs.react.{React => R, ReactDOM => RD}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("react", JSImport.Namespace)
@js.native
object React extends R

@JSImport("react-dom", JSImport.Namespace)
@js.native
object ReactDOM extends RD