package de.tomalbrc.skillcore.impl;

import com.google.gson.*;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.Skill;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.meta.MetaSkill;
import de.tomalbrc.skillcore.io.Json;
import de.tomalbrc.skillcore.registry.MetaSkillRegistry;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class MetaSkillRef {
    private UUID id;
    private final List<String> list;

    public MetaSkillRef() {
        this.list = List.of();
    }

    public MetaSkillRef(String s) {
        this(List.of(s));
    }

    public MetaSkillRef(List<String> list) {
        this.list = list;
        this.id = UUID.randomUUID();
    }

    public void cast(Supplier<SkillTree> tree) {
        List<Skill> list = new ArrayList<>();

        for (String s : this.list) {
            MetaSkillRegistry.getOptional(s).ifPresentOrElse(skill -> skill.castAsync(tree.get()), () -> {
                try {
                    var trimmed = s.trim();
                    var skill = Json.GSON.fromJson(new JsonPrimitive(trimmed), Skill.class);
                    if (skill != null) {
                        list.add(skill);
                    }
                } catch (Exception e) {
                    SkillCore.LOGGER.warn("Could not run or find Metaskill: {}", s);
                }
            });
        }

        if (!list.isEmpty()) {
            var s = new MetaSkill(id.toString(), null, null, null, null, null, 0, null, null, list);
            s.castAsync(tree.get());
        }
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public static class Deserializer implements JsonDeserializer<MetaSkillRef> {
        @Override
        public MetaSkillRef deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List<String> list = new ArrayList<>();
            if (json.isJsonArray()) {
                for (JsonElement element : json.getAsJsonArray()) {
                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                        list.add(element.getAsString());
                    }
                }
            } else if (json.isJsonPrimitive()) {
                list.add(json.getAsString());
            } else {
                throw new JsonParseException("Invalid value for MetaSkillRef: " + json.getAsString());
            }

            return new MetaSkillRef(list);
        }
    }
}
