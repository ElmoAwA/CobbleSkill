package de.tomalbrc.skillcore.io;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.*;

/**
 * Adapts values whose runtime type may differ from their declaration type.
 * Supports aliases for subtype labels.
 */
public class RuntimeTypeAdapterFactoryWithAliases<T> implements TypeAdapterFactory {
    protected final Class<?> baseType;
    protected final String typeFieldName;
    protected final boolean maintainType;

    /**
     * map of label → subtype
     */
    protected final Map<String, Class<?>> labelToSubtype = new LinkedHashMap<>();
    /**
     * map of subtype → canonical label
     */
    protected final Map<Class<?>, String> subtypeToLabel = new LinkedHashMap<>();
    /**
     * map of alias → canonical label
     */
    protected final Map<String, String> aliasToCanonical = new LinkedHashMap<>();

    protected RuntimeTypeAdapterFactoryWithAliases(Class<?> baseType, String typeFieldName, boolean maintainType) {
        if (baseType == null || typeFieldName == null) {
            throw new NullPointerException();
        }
        this.baseType = baseType;
        this.typeFieldName = typeFieldName;
        this.maintainType = maintainType;
    }

    public static <T> RuntimeTypeAdapterFactoryWithAliases<T> of(Class<T> baseType, String typeFieldName, boolean maintainType) {
        return new RuntimeTypeAdapterFactoryWithAliases<>(baseType, typeFieldName, maintainType);
    }

    public static <T> RuntimeTypeAdapterFactoryWithAliases<T> of(Class<T> baseType, String typeFieldName) {
        return new RuntimeTypeAdapterFactoryWithAliases<>(baseType, typeFieldName, false);
    }

    public static <T> RuntimeTypeAdapterFactoryWithAliases<T> of(Class<T> baseType) {
        return new RuntimeTypeAdapterFactoryWithAliases<>(baseType, "type", false);
    }

    /**
     * Registers subtype with canonical label.
     */
    public RuntimeTypeAdapterFactoryWithAliases<T> registerSubtype(Class<? extends T> type, String label) {
        if (type == null || label == null) {
            throw new NullPointerException();
        }
        if (subtypeToLabel.containsKey(type)) {
            throw new IllegalArgumentException("Subtype " + type + " already registered with label " + subtypeToLabel.get(type));
        }
        if (labelToSubtype.containsKey(label) || aliasToCanonical.containsKey(label)) {
            throw new IllegalArgumentException("Label '" + label + "' is already used as a label or alias");
        }

        labelToSubtype.put(label, type);
        subtypeToLabel.put(type, label);
        return this;
    }

    /**
     * Registers subtype with canonical label, and one or more alias labels.
     */
    public RuntimeTypeAdapterFactoryWithAliases<T> registerSubtypeWithAliases(Class<? extends T> type, String canonicalLabel, String... aliases) {
        registerSubtype(type, canonicalLabel);
        for (String alias : aliases) {
            if (alias == null) {
                throw new NullPointerException("Alias cannot be null");
            }
            if (labelToSubtype.containsKey(alias) || aliasToCanonical.containsKey(alias)) {
                throw new IllegalArgumentException("Alias '" + alias + "' is already used as a label or alias");
            }
            aliasToCanonical.put(alias, canonicalLabel);
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> typeToken) {
        if (typeToken == null) {
            return null;
        }
        Class<?> rawType = typeToken.getRawType();
        boolean handle = baseType.equals(rawType) || (rawType != null && baseType.isAssignableFrom(rawType));
        if (!handle) {
            return null;
        }

        final TypeAdapter<JsonElement> jsonElementAdapter = gson.getAdapter(JsonElement.class);
        final Map<String, TypeAdapter<?>> labelToDelegate = new LinkedHashMap<>();
        final Map<Class<?>, TypeAdapter<?>> subtypeToDelegate = new LinkedHashMap<>();

        for (Map.Entry<String, Class<?>> entry : labelToSubtype.entrySet()) {
            TypeAdapter<?> delegate = gson.getDelegateAdapter(this, TypeToken.get(entry.getValue()));
            labelToDelegate.put(entry.getKey(), delegate);
            subtypeToDelegate.put(entry.getValue(), delegate);
        }

        return new TypeAdapter<R>() {
            @Override
            public R read(JsonReader in) throws IOException {
                JsonElement jsonElement = jsonElementAdapter.read(in);
                JsonObject jsonObj = jsonElement.getAsJsonObject();

                JsonElement labelJsonElement;
                if (maintainType) {
                    labelJsonElement = jsonObj.get(typeFieldName);
                } else {
                    labelJsonElement = jsonObj.remove(typeFieldName);
                }

                if (labelJsonElement == null) {
                    throw new JsonParseException("Cannot deserialize " + baseType + " because it does not define a field named " + typeFieldName);
                }
                String label = labelJsonElement.getAsString().toLowerCase(Locale.ROOT);

                // resolve alias to canonical
                if (aliasToCanonical.containsKey(label)) {
                    label = aliasToCanonical.get(label);
                }

                TypeAdapter<R> delegate = (TypeAdapter<R>) labelToDelegate.get(label);
                if (delegate == null) {
                    throw new JsonParseException("Cannot deserialize " + baseType + " subtype named '" + label + "'; did you forget to register a subtype or alias?");
                }

                return delegate.fromJsonTree(jsonElement);
            }

            @Override
            public void write(JsonWriter out, R value) throws IOException {
                Class<?> srcType = value.getClass();
                String label = subtypeToLabel.get(srcType);
                TypeAdapter<R> delegate = (TypeAdapter<R>) subtypeToDelegate.get(srcType);

                if (delegate == null) {
                    throw new JsonParseException("Cannot serialize " + srcType.getName() + "; did you forget to register a subtype?");
                }

                JsonObject jsonObj = delegate.toJsonTree(value).getAsJsonObject();

                if (maintainType) {
                    jsonElementAdapter.write(out, jsonObj);
                    return;
                }

                if (jsonObj.has(typeFieldName)) {
                    throw new JsonParseException("Cannot serialize " + srcType.getName() + " because it already defines a field named " + typeFieldName);
                }

                JsonObject clone = new JsonObject();
                clone.add(typeFieldName, new JsonPrimitive(label));
                for (Map.Entry<String, JsonElement> e : jsonObj.entrySet()) {
                    clone.add(e.getKey(), e.getValue());
                }

                jsonElementAdapter.write(out, clone);
            }
        }.nullSafe();
    }

    public Map<String, List<String>> getCanonicalLabelsWithAliases() {
        Map<String, List<String>> result = new LinkedHashMap<>();

        for (String canonical : labelToSubtype.keySet()) {
            result.put(canonical, new ArrayList<>());
        }

        aliasToCanonical.forEach((alias, canonical) -> {
            result.get(canonical).add(alias);
        });

        result.values().forEach(Collections::sort);

        return result;
    }

    public void printLabels() {
        System.out.println(baseType.getSimpleName() + ":");

        Map<String, List<String>> labelMap = getCanonicalLabelsWithAliases();

        if (labelMap.isEmpty()) {
            System.out.println("No aliases registered.");
            return;
        }

        for (Map.Entry<String, List<String>> entry : labelMap.entrySet()) {
            List<String> aliases = entry.getValue();
            String aliasString = aliases.isEmpty() ? "" : ", Aliases: " + String.join(", ", aliases) + ")";
            System.out.println(entry.getKey() + " " + aliasString);
        }
        System.out.println("----------------------------------------");
    }
}
