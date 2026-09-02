package de.tomalbrc.skillcore.registry;

import com.google.gson.JsonParser;
import de.tomalbrc.skillcore.api.event.SkillCoreRegistrationEvents;
import de.tomalbrc.skillcore.data.MobData;
import de.tomalbrc.skillcore.io.Json;
import de.tomalbrc.skillcore.spawn.CustomSpawner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MobRegistry {
    private static final Map<String, MobData> types = new HashMap<>();

    public static void register(InputStream inputStream) throws IOException {
        var element = JsonParser.parseReader(new InputStreamReader(inputStream));
        MobData mobData = Json.GSON.fromJson(element, MobData.class);
        register(mobData);
    }

    static public void register(MobData data) {
        types.put(data.identifier(), data);
        CustomSpawner.addOverride(data); // will add only if possible
        SkillCoreRegistrationEvents.MOB.invoker().registered(data);
    }

    public static MobData get(String id) {
        return types.get(id);
    }

    public static Optional<MobData> getOptional(String id) {
        return Optional.ofNullable(types.get(id));
    }

    public static Map<String, MobData> all() {
        return types;
    }
}
