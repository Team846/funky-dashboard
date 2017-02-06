package com.lynbrookrobotics.funkydashboard

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.imageio.ImageIO

class ImageStream(name: String)(value: => BufferedImage) extends Dataset[ImageStreamValue] {
  override val definition: DatasetDefinition =
    DatasetDefinition(name, "image-stream")

  override def currentValue: ImageStreamValue = {
    val baos = new ByteArrayOutputStream()

    try {
      ImageIO.write(value, "jpeg", baos)
      baos.flush()
      ImageStreamValue(Base64.getEncoder.encodeToString(baos.toByteArray))
    } catch {
      case _: Exception =>
        ImageStreamValue("")
    }
  }
}
