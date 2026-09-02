package de.tomalbrc.skillcore.io;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.tomalbrc.skillcore.api.Skill;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.tomalbrc.skillcore.io.ParseUtil.*;

public final class SkillAdapterFactory implements TypeAdapterFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!Skill.class.equals(type.getRawType())) return null;

        TypeAdapter<Skill> delegate = gson.getDelegateAdapter(this, TypeToken.get(Skill.class));

        return (TypeAdapter<T>) new TypeAdapter<Skill>() {
            @Override
            public void write(JsonWriter out, Skill value) throws IOException {
                delegate.write(out, value);
            }

            @Override
            public Skill read(JsonReader in) throws IOException {
                JsonElement tree = JsonParser.parseReader(in);

                if (tree.isJsonObject()) {
                    return delegate.fromJsonTree(tree);
                }

                // MM DSL
                List<String> tokens = getTokens(tree);

                Map<String, Object> skillMap = new HashMap<>();

                // mechanic is required as first token
                String mechToken = tokens.getFirst();
                Map<String, Object> mechMap = buildTypeMap(mechToken);
                if (mechToken.equals("delay") && tokens.size() > 1) {
                    mechMap.put("ticks", Integer.parseInt(tokens.get(1)));
                }
                skillMap.put("mechanic", mechMap);

                Map<String, Object> targeterMap = null;
                String triggerEnumName = null;
                String healthCondition = null;
                String chance = null;
                int time = 0;
                String signal = null;
                List<Map<String, Object>> inlineConditions = new ArrayList<>();

                for (int i = 1; i < tokens.size(); i++) {
                    String currentToken = tokens.get(i);
                    if (currentToken == null || currentToken.isEmpty()) continue;
                    char c = currentToken.charAt(0);

                    if (c == '@') {
                        targeterMap = buildTypeMap(currentToken.substring(1));
                    } else if (c == '~') {
                        // normalize trigger to enum name + parse time (example: onTimer:30 or onSignal)
                        String raw = currentToken.startsWith("~on") ? currentToken.substring(3) : currentToken.substring(1);
                        if (raw.contains(":")) {
                            var split = raw.split(":");
                            if (split.length > 1) {
                                var last = split[split.length-1];
                                if (Character.isAlphabetic(last.charAt(0))) {
                                    signal = split[1];
                                } else {
                                    time = Integer.parseInt(last);
                                }
                            }

                            raw = raw.substring(0, raw.lastIndexOf(':'));
                        }
                        String normalized = toUpperUnderscore(raw);
                        triggerEnumName = normalized.startsWith("ON_") ? normalized : "ON_" + normalized;
                    } else if (c == '<' || c == '>' || c == '=') {
                        healthCondition = currentToken;
                    } else if (c == '?') {
                        // inline simple condition
                        // e.g. "?night" -> { "type": "night" }
                        // e.g. "?!night" -> { "type": "night", "action": "false" }
                        // e.g. "?!night{a=1}" -> { "type": "night", "action": "false", "a": 1 }
                        String condName = currentToken.substring(1).trim();
                        String action = "true";
                        String testTarget = "false";
                        if (condName.startsWith("!")) {
                            action = "false";
                            condName = condName.substring(1);
                        }
                        if (condName.startsWith("~")) {
                            testTarget = "true";
                            condName = condName.substring(1);
                        }
                        Map<String, Object> map = new HashMap<>(buildTypeMap(condName));
                        map.put("action", action);
                        map.put("test_target", testTarget);
                        inlineConditions.add(map);
                    } else {
                        if (StringUtils.isNumeric(currentToken)) {
                            chance = currentToken;
                        }
                    }
                }

                if (targeterMap != null) skillMap.put("targeter", targeterMap);
                if (triggerEnumName != null) skillMap.put("trigger", triggerEnumName);
                if (healthCondition != null) skillMap.put("healthCondition", healthCondition);
                if (chance != null) skillMap.put("chance", chance);
                if (time != 0) skillMap.put("time", time);
                if (signal != null) skillMap.put("signal", signal);
                if (!inlineConditions.isEmpty()) skillMap.put("conditions", inlineConditions);


                JsonElement generated = gson.toJsonTree(skillMap);
                return delegate.fromJsonTree(generated);
            }
        };
    }

    private @NotNull List<String> getTokens(JsonElement tree) {
        if (!tree.isJsonPrimitive() || !tree.getAsJsonPrimitive().isString()) {
            throw new JsonParseException("Skill must be an object or MM DSL string");
        }

        String skillStr = tree.getAsString().trim();
        if (skillStr.isEmpty()) throw new JsonParseException("Empty skill string");

        List<String> tokens = tokenizeTopLevel(skillStr);
        if (tokens.isEmpty()) throw new JsonParseException("Invalid skill string: " + skillStr);
        return tokens;
    }
}
