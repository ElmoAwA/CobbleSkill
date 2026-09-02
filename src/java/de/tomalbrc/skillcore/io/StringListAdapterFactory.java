package de.tomalbrc.skillcore.io;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringListAdapterFactory implements TypeAdapterFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!List.class.isAssignableFrom(type.getRawType())) {
            return null;
        }
        if (!type.getType().getTypeName().contains("String")) {
            return null;
        }

        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

        return (TypeAdapter<T>) new TypeAdapter<List<String>>() {
            @Override
            public void write(JsonWriter out, List<String> value) throws IOException {
                out.beginArray();
                for (String s : value) {
                    out.value(s);
                }
                out.endArray();
            }

            @Override
            public List<String> read(JsonReader in) throws IOException {
                JsonElement element = elementAdapter.read(in);

                if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                    String raw = element.getAsString().trim();
                    if (raw.isEmpty()) {
                        return List.of();
                    }

                    return Arrays.stream(raw.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .toList();
                }

                if (element.isJsonArray()) {
                    List<String> list = new ArrayList<>();
                    for (JsonElement e : element.getAsJsonArray()) {
                        list.add(e.getAsString());
                    }
                    return list;
                }

                throw new JsonParseException("Expected a comma-delimited string or a JSON array");
            }
        };
    }
}
