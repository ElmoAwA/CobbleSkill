package de.tomalbrc.skillcore.registry;

import com.google.gson.JsonParser;
import de.tomalbrc.skillcore.api.event.SkillCoreRegistrationEvents;
import de.tomalbrc.skillcore.data.DropTable;
import de.tomalbrc.skillcore.io.Json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DropTableRegistry {
    private static final Map<String, DropTable> types = new HashMap<>();

    public static void register(InputStream inputStream) throws IOException {
        var element = JsonParser.parseReader(new InputStreamReader(inputStream));
        var data = Json.GSON.fromJson(element, DropTable.class);
        register(data);
    }

    static public void register(DropTable data) {
        types.put(data.identifier(), data);
        SkillCoreRegistrationEvents.DROP_TABLE.invoker().registered(data);
    }

    public static DropTable get(String id) {
        return types.get(id);
    }

    public static Map<String, DropTable> all() {
        return types;
    }
}
