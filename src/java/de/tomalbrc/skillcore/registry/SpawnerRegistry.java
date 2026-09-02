package de.tomalbrc.skillcore.registry;

import com.google.gson.JsonParser;
import de.tomalbrc.skillcore.api.event.SkillCoreRegistrationEvents;
import de.tomalbrc.skillcore.data.RandomSpawnData;
import de.tomalbrc.skillcore.io.Json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class SpawnerRegistry {
    private static final Map<String, RandomSpawnData> types = new HashMap<>();

    public static void register(InputStream inputStream) throws IOException {
        var element = JsonParser.parseReader(new InputStreamReader(inputStream));
        var data = Json.GSON.fromJson(element, RandomSpawnData.class);
        register(data);
    }

    static public void register(RandomSpawnData data) {
        types.put(data.identifier(), data);
        SkillCoreRegistrationEvents.SPAWNER.invoker().registered(data);
    }

    public static RandomSpawnData get(String id) {
        return types.get(id);
    }

    public static Map<String, RandomSpawnData> all() {
        return types;
    }
}
