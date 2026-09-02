package de.tomalbrc.skillcore.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.tomalbrc.skillcore.io.ParseUtil;
import de.tomalbrc.skillcore.util.BukkitIdConverter;
import net.minecraft.world.entity.EquipmentSlot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public record EquipmentEntry(
        CustomItemData item,
        EquipmentSlot slot
) {
    public static class AdapterFactory implements TypeAdapterFactory {

        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (!EquipmentEntry.class.equals(type.getRawType())) {
                return null;
            }

            final TypeAdapter<EquipmentEntry> delegate = gson.getDelegateAdapter(this, TypeToken.get(EquipmentEntry.class));
            final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

            return (TypeAdapter<T>) new TypeAdapter<EquipmentEntry>() {
                @Override
                public void write(JsonWriter out, EquipmentEntry value) throws IOException {
                    delegate.write(out, value);
                }

                @Override
                public EquipmentEntry read(JsonReader in) throws IOException {
                    JsonElement element = elementAdapter.read(in);

                    if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                        String str = element.getAsString();

                        var tokens = ParseUtil.tokenizeTopLevel(str);

                        Map<String, Object> itemMap = ParseUtil.buildTypeMap(tokens.getFirst(), "identifier");

                        Map<String, Object> root = new HashMap<>();
                        root.put("item", itemMap);


                        String slotStr = tokens.size() < 2 ? "MAINHAND" : tokens.getLast();

                        EquipmentSlot slot = BukkitIdConverter.slot(slotStr).orElseGet(() -> EquipmentSlot.valueOf(slotStr.toUpperCase()));
                        root.put("slot", slot.getName());

                        element = gson.toJsonTree(root);
                    }

                    return delegate.fromJsonTree(element);
                }
            };
        }
    }
}
