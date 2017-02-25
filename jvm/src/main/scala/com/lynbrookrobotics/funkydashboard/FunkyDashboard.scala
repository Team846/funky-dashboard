package com.lynbrookrobotics.funkydashboard

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws.{Message, TextMessage, UpgradeToWebSocket}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import play.api.libs.json.Json

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

class FunkyDashboard(implicit materializer: ActorMaterializer, ec: ExecutionContext) {
  private val datasetGroups = mutable.Map[String, DatasetGroup]()

  val startTime = System.currentTimeMillis()

  val source = Source.tick(0 millis, 125 millis, ()).map { _ =>
    val time = System.currentTimeMillis()
    val toSend = TimedValue(time, datasetGroups.toMap.map(t => t._1 -> t._2.currentValue))
    TextMessage(Json.toJson(toSend).toString())
  }

  private def handleIncomingString(string: String): Unit = {
    val List(groupName, datasetName, value) = Json.parse(string).as[List[String]]
    datasetGroups.get(groupName).foreach { g =>
      g.dataset(datasetName).foreach { d =>
        d.handleIncomingData(value)
      }
    }
  }

  private val sink = Sink.foreach[Message] {
    case TextMessage.Strict(msg) ⇒
      handleIncomingString(msg)
    case TextMessage.Streamed(stream) => stream
      .limit(100) // Max frames we are willing to wait for
      .completionTimeout(5 seconds) // Max time until last frame
      .runFold("")(_ + _) // Merges the frames
      .map(s => handleIncomingString(s))

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
            Json.toJson(datasetGroups.values.toVector.map(_.properties)).toString()
          )
        ))
      } ~ path("datastream") {
      optionalHeaderValueByType[UpgradeToWebSocket](()) {
        case Some(header) =>
          complete(header.handleMessagesWithSinkSource(sink, source))
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
