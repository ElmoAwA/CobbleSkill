package de.tomalbrc.skillcore.io;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonResourceLoader {

    private static final Yaml YAML = new Yaml();

    public static <T> List<T> loadAll(Path folder, Class<T> clazz) throws IOException {
        if (!Files.isDirectory(folder)) {
            throw new IllegalArgumentException("Not a directory: " + folder);
        }

        List<T> result = new ArrayList<>();

        try (var paths = Files.list(folder)) {
            for (Path file : paths.toList()) {
                if (!Files.isRegularFile(file)) continue;

                String name = file.getFileName().toString().toLowerCase();
                JsonElement jsonElement;

                if (name.endsWith(".yaml") || name.endsWith(".yml")) {
                    jsonElement = yamlToJsonTree(file);
                } else if (name.endsWith(".json")) {
                    jsonElement = JsonParser.parseString(Files.readString(file));
                } else {
                    continue; // skip non-resource files
                }

                result.addAll(parseResources(jsonElement, clazz));
            }
        }

        return result;
    }

    /**
     * Converts a YAML file into a Gson JsonElement using SnakeYAML.
     */
    private static JsonElement yamlToJsonTree(Path file) throws IOException {
        try (Reader reader = Files.newBufferedReader(file)) {
            Object data = YAML.load(reader);
            return Json.GSON.toJsonTree(data);
        }
    }

    private static <T> List<T> parseResources(JsonElement jsonElement, Class<T> clazz) {
        List<T> items = new ArrayList<>();

        if (!jsonElement.isJsonObject()) {
            return items;
        }

        JsonObject obj = jsonElement.getAsJsonObject();

        boolean isMapForm = obj.entrySet().stream()
                .allMatch(e -> e.getValue().isJsonObject());

        if (isMapForm) {
            for (var entry : obj.entrySet()) {
                JsonObject value = entry.getValue().getAsJsonObject();
                if (!value.has("identifier")) {
                    value.addProperty("identifier", entry.getKey());
                }
                items.add(Json.GSON.fromJson(value, clazz));
            }
        } else {
            items.add(Json.GSON.fromJson(obj, clazz));
        }

        return items;
    }
}
