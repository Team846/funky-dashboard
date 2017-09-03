package com.lynbrookrobotics.funkydashboard

import java.util.{Timer, TimerTask}

import io.undertow.Undertow
import play.api.libs.json.Json

import scala.collection.mutable
import scala.language.postfixOps
import io.undertow.Handlers._
import io.undertow.server.handlers.resource.ClassPathResourceManager
import io.undertow.websockets.core._

class FunkyDashboard(updatePeriod: Int, port: Int) {
  private val datasetGroups = mutable.Map[String, DatasetGroup]()

  private var listeners: List[String => Unit] = List.empty

  private val timer = new Timer
  timer.schedule(new TimerTask {
    override def run(): Unit = {
      val time = System.currentTimeMillis()
      val msg = Json.toJson(TimedValue(time, datasetGroups.toMap.map(t => t._1 -> t._2.currentValue))).toString
      listeners.foreach(_.apply(msg))
    }
  }, 0, updatePeriod)

  private def handleIncomingString(string: String): Unit = {
    val List(groupName, datasetName, value) = Json.parse(string).as[List[String]]
    datasetGroups.get(groupName).foreach { g =>
      g.dataset(datasetName).foreach { d =>
        d.handleIncomingData(value)
      }
    }
  }

  def handleConnect(channel: WebSocketChannel): Unit = {
    val sendListener = (msg: String) => {
      WebSockets.sendText(msg, channel, null)
    }

    channel.getReceiveSetter.set(new AbstractReceiveListener {
      override def onFullTextMessage(channel: WebSocketChannel, message: BufferedTextMessage): Unit = {
        handleIncomingString(message.getData)
      }

      override def onClose(webSocketChannel: WebSocketChannel, channel: StreamSourceFrameChannel): Unit = {
        listeners = listeners.filterNot(_ eq sendListener)
      }
    })

    sendListener(Json.toJson(datasetGroups.values.toVector.map(_.properties)).toString)

    listeners = sendListener :: listeners

    channel.resumeReceives()
  }

  private val server = Undertow.builder()
    .addHttpListener(port, "0.0.0.0")
    .setIoThreads(1)
    .setWorkerThreads(1)
    .setDirectBuffers(false)
    .setBufferSize(512)
    .setHandler(
      path.addExactPath("/datastream", websocket(
        (_, channel) => {
          handleConnect(channel)
        }
      )).addPrefixPath("/", resource(
        new ClassPathResourceManager(getClass.getClassLoader, "META-INF/resources")
      ).addWelcomeFiles("index.html"))
    ).build

  def datasetGroup(key: String): DatasetGroup = datasetGroups.getOrElseUpdate(key, new DatasetGroup(key))

  def start(): Unit = {
    server.start()
  }

  def stop(): Unit = {
    timer.cancel()
    server.stop()
  }
}
