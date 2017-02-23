package com.lynbrookrobotics.funkydashboard

class DatasetGroup(name: String) {
  private var datasets: List[Dataset[_]] = List.empty

  def properties = DatasetGroupDefinition(name, datasets.map(_.definition))
  def currentValue: Map[String, String] = datasets.map(d => d.definition.name -> d.currentValueJSON).toMap
  def dataset(name: String): Option[Dataset[_]] = datasets.find(_.definition.name == name)

  def addDataset(dataset: Dataset[_]): Unit = {
    datasets = datasets :+ dataset
  }
}
