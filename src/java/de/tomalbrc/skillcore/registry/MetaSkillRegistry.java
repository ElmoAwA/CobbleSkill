package de.tomalbrc.skillcore.registry;

import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import de.tomalbrc.skillcore.api.Skill;
import de.tomalbrc.skillcore.api.event.SkillCoreRegistrationEvents;
import de.tomalbrc.skillcore.api.meta.MetaSkill;
import de.tomalbrc.skillcore.io.Json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class MetaSkillRegistry {
    private static final Map<String, MetaSkill> types = new HashMap<>();

    public static void register(InputStream inputStream) throws IOException {
        var element = JsonParser.parseReader(new InputStreamReader(inputStream));
        var data = Json.GSON.fromJson(element, MetaSkill.class);
        register(data);
    }

    static public void register(MetaSkill data) {
        types.put(data.identifier(), data);
        SkillCoreRegistrationEvents.METASKILL.invoker().registered(data);
    }

    public static MetaSkill get(String id) {
        return types.get(id);
    }

    public static Optional<MetaSkill> getOptional(String id) {
        return Optional.ofNullable(types.get(id));
    }

    public static Optional<MetaSkill> fromInline(List<String> id) {
        List<Skill> list = new ArrayList<>();
        for (String s : id) {
            if (s.startsWith("- "))
                s = s.substring(2).trim();

            var trimmed = s.trim();
            if (!trimmed.isBlank()) {
                var skill = Json.GSON.fromJson(new JsonPrimitive(trimmed), Skill.class);
                if (skill != null) {
                    list.add(skill);
                }
            }
        }

        if (!list.isEmpty()) {
            return Optional.of(new MetaSkill(UUID.randomUUID().toString(), null, null, null, null, null, 0, null, null, list));
        }

        return Optional.empty();
    }

    public static Map<String, MetaSkill> all() {
        return types;
    }
}
