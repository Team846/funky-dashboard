package com.lynbrookrobotics.funkydashboard

class TableDataset(name: String)(value: => List[(String, String)]) extends Dataset[List[TablePair]] {
  override val definition =
    DatasetDefinition(name, "table")

  override def currentValue = value.map(t => TablePair(t._1, t._2))
}
