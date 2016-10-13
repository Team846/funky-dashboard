package com.lynbrookrobotics.funkydashboard

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws.{TextMessage, UpgradeToWebsocket}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.collection.mutable
import scala.concurrent.duration._
import upickle.default._

object FunkyDashboard {
  private val datasetGroups = mutable.Map[String, DatasetGroup]()

  val startTime = System.currentTimeMillis()

  val source = Source.tick(0 millis, 125 millis, ()).map { _ =>
    val time = (System.currentTimeMillis() - startTime).toDouble / 1000
    val toSend = (time, datasetGroups.toMap.map(t => t._1 -> t._2.currentValue))
    TextMessage(write(toSend))
  }

  val route = get {
    pathSingleSlash {
      getFromResource("META-INF/resources/index.html")
    } ~ pathPrefix("") {
      encodeResponse(getFromResourceDirectory("META-INF/resources"))
    } ~
    path("datasets.json") {
      complete(HttpResponse(
        entity = HttpEntity(ContentTypes.`application/json`, write(datasetGroups.values.toVector.map(_.properties)))
      ))
    } ~ path("datastream") {
      optionalHeaderValueByType[UpgradeToWebsocket](()) {
        case Some(header) => complete(header.handleMessagesWithSinkSource(Sink.ignore, source))
        case None => complete(HttpResponse(status = StatusCodes.BadRequest, entity = "Expected websocket request"))
      }
    }
  }

  def datasetGroup(key: String) = datasetGroups.getOrElseUpdate(key, new DatasetGroup(key))
}
