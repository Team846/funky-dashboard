package com.lynbrookrobotics.funkydashboard;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public abstract class Dataset {
    String name;
    String type;

    public String getName() {
        return name;
    }

    public Dataset(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public JsonValue toJSON() {
        return Json.object().add("name", name).add("type", type).add("properties", extraPropertiesJSON());
    }

    public abstract JsonValue extraPropertiesJSON();
    public abstract JsonValue currentValueJSON();
}
