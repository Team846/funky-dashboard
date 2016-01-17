package com.lynbrookrobotics.funkydashboard

import scala.scalajs.js

@js.native
trait DatasetGroup extends js.Object {
  val name: String = js.native
  val datasets: js.Array[DatasetDefinition] = js.native
}

@js.native
trait DatasetDefinition extends js.Object {
  val name: String = js.native
  val `type`: String = js.native
  val properties: js.Any = js.native
}