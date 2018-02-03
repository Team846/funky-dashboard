package com.lynbrookrobotics.funkydashboard

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class ImageStream(name: String)(value: => BufferedImage) extends Dataset[String] {
  override val definition: DatasetDefinition =
    DatasetDefinition(name, "image-stream")

  override def currentValue = {
    val baos = new ByteArrayOutputStream()

    try {
      ImageIO.write(value, "jpeg", baos)
      baos.flush()
      Base64.getEncoder.encodeToString(baos.toByteArray)
    } catch {
      case _: Exception => ""
    }
  }
}
