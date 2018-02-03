package com.lynbrookrobotics.funkydashboard

import java.io.{BufferedReader, BufferedWriter, InputStreamReader, OutputStreamWriter, PrintWriter}
import java.lang.Thread.UncaughtExceptionHandler
import java.net.{InetSocketAddress, ServerSocket}

import scala.collection.mutable
import scala.language.postfixOps
import argonaut._
import Argonaut._
import ArgonautShapeless._

class FunkyDashboard(updatePeriod: Int, port: Int) {
  private val datasetGroups = mutable.Map[String, DatasetGroup]()

  private var listeners: List[String => Unit] = List.empty

  private val timer = new Thread(new Runnable {
    override def run(): Unit = {
      while (!Thread.interrupted()) {
        try {
          if (listeners.nonEmpty) {
            val time = System.currentTimeMillis()
            val msg = TimedValue(time, datasetGroups.toMap.map(t => t._1 -> t._2.currentValue)).jencode.toString
            listeners.foreach(_.apply(msg))
          }

          Thread.sleep(updatePeriod)
        } catch {
          case e =>
            e.printStackTrace()
        }
      }
    }
  })

  private def handleIncomingString(string: String): Unit = {
    val List(groupName, datasetName, value) = string.decodeOption[List[String]].get
    datasetGroups.get(groupName).foreach { g =>
      g.dataset(datasetName).foreach { d =>
        d.handleIncomingData(value)
      }
    }
  }

  def datasetGroup(key: String): DatasetGroup = datasetGroups.getOrElseUpdate(key, new DatasetGroup(key))

  val serverSocket = new ServerSocket
  serverSocket.setReuseAddress(true)
  serverSocket.bind(new InetSocketAddress(8080))

  def serverLoop(): Unit = {
    try {
      val socket = serverSocket.accept()
      val reader = new BufferedReader(new InputStreamReader(socket.getInputStream))
      val writer = new PrintWriter(socket.getOutputStream)
      val path = reader.readLine().split(' ')(1)
      while (reader.ready() && !reader.readLine().isEmpty) {}

      var body = ""
      while (reader.ready()) body = body + reader.read().toChar

      def writeOK(extraHeaders: String, content: Array[Byte]): Unit = {
        writer.write(
          s"""HTTP/1.1 200 OK
             |$extraHeaders
             |""".stripMargin
        )

        writer.write("\r\n")
        writer.flush()

        if (content.nonEmpty) {
          socket.getOutputStream.write(content)
          socket.getOutputStream.flush()
        }

        reader.close()
        writer.close()
        socket.close()
      }

      if (path == "/sse") {
        writer.write(
          """HTTP/1.1 200 OK
            |Cache-Control: no-cache,public
            |Content-Type: text/event-stream
            |Connection: keep-alive
            |Language: en-US
            |Charset: UTF-8
            |
            |""".stripMargin
        )
        writer.flush()

        new Thread(new Runnable {
          override def run(): Unit = {
            val in = socket.getInputStream
            while (in.read() != -1) {}
            socket.close()
          }
        }).start()

        var list: String => Unit = null
        list = (msg: String) => {
          if (!socket.isClosed) {
            writer.print(s"data: $msg\n\n")
            writer.flush()
          } else {
            listeners = listeners.filterNot(_ == list)
            reader.close()
            writer.close()
            socket.close()
          }
        }

        list(datasetGroups.values.toVector.map(_.properties).jencode.toString)

        listeners = list :: listeners
      } else if (path == "/post") {
        handleIncomingString(body)
        writeOK(
          """Cache-Control: no-cache,public""".stripMargin,
          Array.empty
        )
      } else if (path == "/") {
        writeOK(
          """Cache-Control: no-cache,public
            |Content-Type: text/html
            |Language: en-US
            |Charset: UTF-8
            |Content-Encoding: gzip""".stripMargin,
          Resources.resources("index.html")
        )
      } else {
        val mimeType = path.tail.split('.').last match {
          case "css" => "text/css"
          case "js" => "application/javascript"
          case "png" => "image/png"
          case _ => "text/plain"
        }

        writeOK(
          s"""Cache-Control: no-cache,public
             |Content-Type: $mimeType
             |Content-Encoding: gzip""".stripMargin,
          Resources.resources.getOrElse(path.tail, Array.empty)
        )
      }
    } catch {
      case t: Throwable =>
        t.printStackTrace()
    }
  }

  private var serverThread: Thread = null
  private var isRunning: Boolean = false

  def start(): Unit = {
    isRunning = true
    serverThread = new Thread(new Runnable {
      override def run(): Unit = {
        while (isRunning) {
          serverLoop()
        }
      }
    })
    serverThread.start()
    timer.start()
  }

  def stop(): Unit = {
    isRunning = false
    serverSocket.close()
    serverThread.stop()
    timer.stop()
  }
}
