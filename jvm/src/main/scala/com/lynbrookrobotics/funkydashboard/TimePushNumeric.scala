package com.lynbrookrobotics.funkydashboard

import scala.collection.mutable

class TimePushNumeric(name: String) extends Dataset[Seq[TimedValue[Double]]] {
  private val queue = mutable.Queue[TimedValue[Double]]()

  def pushValue(v: Double) = {
    queue.enqueue(TimedValue(System.currentTimeMillis(), v))
  }

  override val definition = DatasetDefinition(name, "time-push")

  override def currentValue = queue
}
