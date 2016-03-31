package com.lynbrookrobotics.funkydashboard;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.function.Supplier;

import javax.imageio.ImageIO;

public class ImageStream extends Dataset {
    private Supplier<BufferedImage> currentValue;

    public ImageStream(String name, Supplier<BufferedImage> currentValue) {
        super(name, "image-stream");
        this.currentValue = currentValue;
    }

    @Override
    public JsonObject extraPropertiesJSON() {
        return Json.object();
    }

    @Override
    public JsonValue currentValueJSON() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ImageIO.write(currentValue.get(), "png", baos);
            baos.flush();
            return Json.value(Base64.getEncoder().encodeToString(baos.toByteArray()));
        } catch (Throwable e) {
            return Json.value("");
        }
    }
}
