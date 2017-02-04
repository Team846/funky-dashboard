package com.lynbrookrobotics.funkydashboard

import akka.stream.scaladsl.{Sink, Source}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws.{Message, TextMessage, UpgradeToWebSocket}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.collection.mutable
import scala.concurrent.duration._
import upickle.default._

import scala.language.postfixOps

class FunkyDashboard {
  private val datasetGroups = mutable.Map[String, DatasetGroup]()

  val startTime = System.currentTimeMillis()

  val source = Source.tick(0 millis, 125 millis, ()).map { _ =>
    val time = System.currentTimeMillis()
    val toSend = (time, datasetGroups.toMap.map(t => t._1 -> t._2.currentValue))
    TextMessage(write(toSend))
  }

  private val sink = Sink.foreach[Message] {
    case t: TextMessage =>
      val (groupName, datasetName, value) = read[(String, String, String)](t.getStrictText)
      datasetGroups.get(groupName).foreach { g =>
        g.dataset(datasetName).foreach { d =>
          d.handleIncomingData(value)
        }
      }
    case _ =>
  }

  val route: Route = get {
    pathSingleSlash {
      getFromResource("META-INF/resources/index.html")
    } ~ pathPrefix("") {
      encodeResponse(getFromResourceDirectory("META-INF/resources"))
    } ~
    path("datasets.json") {
      complete(HttpResponse(
        entity = HttpEntity(
          ContentTypes.`application/json`,
          write(datasetGroups.values.toVector.map(_.properties))
        )
      ))
    } ~ path("datastream") {
      optionalHeaderValueByType[UpgradeToWebSocket](()) {
        case Some(header) =>
          complete(header.handleMessagesWithSinkSource(Sink.ignore, source))
        case None =>
          complete(HttpResponse(
            status = StatusCodes.BadRequest,
            entity = "Expected websocket request"
          ))
      }
    }
  }

  def datasetGroup(key: String) = datasetGroups.getOrElseUpdate(key, new DatasetGroup(key))
}
