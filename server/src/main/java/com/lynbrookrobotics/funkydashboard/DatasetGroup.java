package com.lynbrookrobotics.funkydashboard;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;

import java.util.ArrayList;

public class DatasetGroup {
    private String name;
    private ArrayList<Dataset> datasets = new ArrayList<>();

    public DatasetGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addDataset(Dataset dataset) {
        datasets.add(dataset);
    }

    public JsonValue toJSON() {
        return Json.object().
            add("name", name).
            add(
                "datasets",
                datasets.stream().map(Dataset::toJSON).reduce(
                    Json.array().asArray(),
                    JsonArray::add,
                    (acc1, acc2) -> {
                        throw new RuntimeException("Merging arrays is not yet implemented");
                    }
                )
            );
    }

    public JsonValue currentDatasetValues() {
        return datasets.stream().reduce(
                Json.object(),
                (acc, cur) -> acc.add(cur.getName(), cur.currentValueJSON()),
                (acc1, acc2) -> {
                    throw new RuntimeException("Merging objects is not yet implemented");
                }
        );
    }
}
