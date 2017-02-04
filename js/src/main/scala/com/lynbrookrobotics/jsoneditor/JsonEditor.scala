package com.lynbrookrobotics.jsoneditor

import japgolly.scalajs.react._

import com.payalabs.scalajs.react.bridge.ReactBridgeComponent

import scala.scalajs.js

case class JsonEditor(id: js.UndefOr[String]  = js.undefined,
                      className: js.UndefOr[String] = js.undefined,
                      ref: js.UndefOr[String] = js.undefined,
                      key: js.UndefOr[Any] = js.undefined,
                      value: js.UndefOr[js.Dynamic],
                      propagateChanges: js.UndefOr[js.Dynamic => Unit],
                      styling: js.UndefOr[js.Dynamic] = js.undefined) extends ReactBridgeComponent
