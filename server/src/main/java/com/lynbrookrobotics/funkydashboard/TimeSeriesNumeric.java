package com.lynbrookrobotics.funkydashboard;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.util.function.Supplier;

public class TimeSeriesNumeric<T extends Number> extends Dataset {
    Supplier<T> currentValue;
    String numberType;

    public TimeSeriesNumeric(String name, Supplier<T> currentValue) {
        super(name, "time-series-numeric");
        this.currentValue = currentValue;

        T tempValue = currentValue.get();
        if (tempValue instanceof Byte) {
            numberType = "byte";
        } else if (tempValue instanceof Double) {
            numberType = "double";
        } else if (tempValue instanceof Float) {
            numberType = "float";
        } else if (tempValue instanceof Integer) {
            numberType = "integer";
        } else if (tempValue instanceof Long) {
            numberType = "long";
        } else {
            numberType = "short";
        }
    }

    @Override
    public JsonObject extraPropertiesJSON() {
        return Json.object().add("numberType", numberType);
    }

    @Override
    public JsonValue currentValueJSON() {
        return Json.value(currentValue.get().toString());
    }
}
