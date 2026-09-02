package de.tomalbrc.skillcore.io;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.condition.Condition;
import de.tomalbrc.skillcore.impl.condition.AlwaysTrueCondition;

import java.io.IOException;
import java.util.*;

public final class ConditionAdapterFactoryWithAliases extends RuntimeTypeAdapterFactoryWithAliases<Condition> {
    public ConditionAdapterFactoryWithAliases(Class<?> baseType, String typeFieldName, boolean maintainType) {
        super(baseType, typeFieldName, maintainType);
    }

    public static  ConditionAdapterFactoryWithAliases of(String typeFieldName) {
        return new ConditionAdapterFactoryWithAliases(Condition.class, typeFieldName, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> typeToken) {
        if (typeToken == null) {
            return null;
        }
        Class<?> rawType = typeToken.getRawType();
        boolean handle =
                baseType.equals(rawType) || (rawType != null && baseType.isAssignableFrom(rawType));
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
                JsonObject jsonObj;

                if (!jsonElement.isJsonObject())
                    jsonObj = parseConditionString(gson, jsonElement.getAsString());
                else
                    jsonObj = jsonElement.getAsJsonObject();

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
                    //throw new JsonParseException("Cannot deserialize " + baseType + " subtype named '" + label + "'; did you forget to register a subtype or alias?");
                    SkillCore.LOGGER.error("Could not find condition '{}', defaulting to alwaysTrue", label);
                    return (R)new AlwaysTrueCondition();
                }

                return delegate.fromJsonTree(jsonObj);
            }

            @Override
            public void write(JsonWriter out, R value) throws IOException {
                Class<?> srcType = value.getClass();
                String label = subtypeToLabel.get(srcType);
                TypeAdapter<R> delegate = (TypeAdapter<R>) subtypeToDelegate.get(srcType);

                if (delegate == null) {
                    throw new JsonParseException(
                            "Cannot serialize " + srcType.getName() + "; did you forget to register a subtype?");
                }

                JsonObject jsonObj = delegate.toJsonTree(value).getAsJsonObject();

                if (maintainType) {
                    jsonElementAdapter.write(out, jsonObj);
                    return;
                }

                if (jsonObj.has(typeFieldName)) {
                    throw new JsonParseException(
                            "Cannot serialize " + srcType.getName()
                                    + " because it already defines a field named " + typeFieldName);
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

    /**
     * Parse condition expression strings into JsonObject representation.
     *
     * Supports:
     *  - atomic conditions like "night", "night true", "fieldofview{angle=165} false"
     *  - OR using "||" -> { "type":"any", "conditions": [...] }
     *  - AND using "&&" -> { "type":"all", "conditions": [...] }
     *  - nested parentheses "( ... )"
     *
     * Uses ParseUtil.tokenizeTopLevel / ParseUtil.buildTypeMap for atomic parsing.
     */
    private JsonObject parseConditionString(Gson gson, String asString) {
        if (asString == null) throw new JsonParseException("Null condition string");
        return parseExpression(gson, asString.trim());
    }

    private JsonObject parseExpression(Gson gson, String expr) {
        expr = expr.trim();
        if (expr.isEmpty()) throw new JsonParseException("Empty condition expression");

        while (expr.startsWith("(")) {
            int match = findMatchingParen(expr, 0);
            if (match == expr.length() - 1) {
                expr = expr.substring(1, expr.length() - 1).trim();
            } else {
                break;
            }
        }

        List<String> orParts = splitTopLevel(expr, "||");
        if (orParts.size() > 1) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "any");
            JsonArray arr = new JsonArray();
            for (String part : orParts) {
                arr.add(parseExpression(gson, part));
            }
            obj.add("conditions", arr);
            return obj;
        }

        List<String> andParts = splitTopLevel(expr, "&&");
        if (andParts.size() > 1) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "all");
            JsonArray arr = new JsonArray();
            for (String part : andParts) {
                arr.add(parseExpression(gson, part));
            }
            obj.add("conditions", arr);
            return obj;
        }

        List<String> tokens = ParseUtil.tokenizeTopLevel(expr);
        if (tokens.isEmpty()) throw new JsonParseException("Cannot parse condition: " + expr);
        Map<String, Object> map = new HashMap<>(ParseUtil.buildTypeMap(tokens.get(0)));
        String action = tokens.size() > 1 ? tokens.get(1) : null;
        String skill = tokens.size() > 2 ? tokens.get(2) : null;

        if (action != null) map.put("action", action);
        if (skill != null) map.put("metaskill", skill);

        return gson.toJsonTree(map).getAsJsonObject();
    }

    private List<String> splitTopLevel(String s, String delimiter) {
        List<String> parts = new ArrayList<>();
        int len = s.length();
        int dlen = delimiter.length();
        int last = 0;
        int paren = 0, brace = 0, bracket = 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c == '(') paren++;
            else if (c == ')') paren = Math.max(0, paren - 1);
            else if (c == '{') brace++;
            else if (c == '}') brace = Math.max(0, brace - 1);
            else if (c == '[') bracket++;
            else if (c == ']') bracket = Math.max(0, bracket - 1);

            if (paren == 0 && brace == 0 && bracket == 0) {
                // check delimiter at this position
                if (i + dlen <= len && s.regionMatches(i, delimiter, 0, dlen)) {
                    parts.add(s.substring(last, i).trim());
                    i += dlen - 1; // skip past delimiter
                    last = i + 1;
                }
            }
        }
        parts.add(s.substring(last).trim());
        return parts;
    }

    private int findMatchingParen(String s, int startIndex) {
        if (s == null || startIndex < 0 || startIndex >= s.length() || s.charAt(startIndex) != '(') return -1;
        int depth = 0;
        for (int i = startIndex; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }
}
