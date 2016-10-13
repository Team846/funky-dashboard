package com.lynbrookrobotics.funkydashboard

import upickle.default._

class DatasetGroup(name: String) {
  private var datasets: List[Dataset[_]] = List.empty

  def properties = DatasetGroupDefinition(name, datasets.map(d => write(d.definition)))
  def currentValue: Map[String, String] = datasets.map(d => d.definition.name -> d.currentValueJSON).toMap

  def addDataset(dataset: Dataset[_]): Unit = {
    datasets = datasets :+ dataset
  }
}
