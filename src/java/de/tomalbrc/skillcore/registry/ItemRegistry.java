package de.tomalbrc.skillcore.registry;

import com.google.gson.JsonParser;
import de.tomalbrc.skillcore.api.event.SkillCoreRegistrationEvents;
import de.tomalbrc.skillcore.data.ItemData;
import de.tomalbrc.skillcore.io.Json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {
    private static final Map<String, ItemData> types = new HashMap<>();

    public static void register(InputStream inputStream) throws IOException {
        var element = JsonParser.parseReader(new InputStreamReader(inputStream));
        var data = Json.GSON.fromJson(element, ItemData.class);
        register(data);
    }

    static public void register(ItemData data) {
        types.put(data.identifier(), data);
        SkillCoreRegistrationEvents.ITEM.invoker().registered(data);
    }

    public static ItemData get(String id) {
        return types.get(id);
    }

    public static Map<String, ItemData> all() {
        return types;
    }
}
