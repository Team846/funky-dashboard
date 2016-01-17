package akka.http.impl.util

import akka.actor.Cancellable
import akka.http.impl.util.JavaMapping.Implicits._
import akka.http.javadsl.model.ws.{Message, UpgradeToWebsocket}
import akka.http.javadsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.javadsl.{Sink, Source}

import scala.concurrent.Future

object WebsocketConstructor {
  def handleWebsocketRequestWith(request: HttpRequest, source: Source[Message, Cancellable], sink: Sink[Message, Future[Unit]]): HttpResponse = {
    request.asScala.header[UpgradeToWebsocket] match {
      case Some(header) ⇒ header.handleMessagesWith(sink, source)
      case None ⇒ HttpResponse.create().withStatus(StatusCodes.BAD_REQUEST).withEntity("Expected websocket request")
    }
  }
}
