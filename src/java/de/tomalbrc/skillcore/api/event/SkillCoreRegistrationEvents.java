package de.tomalbrc.skillcore.api.event;

import de.tomalbrc.skillcore.api.meta.MetaSkill;
import de.tomalbrc.skillcore.data.DropTable;
import de.tomalbrc.skillcore.data.ItemData;
import de.tomalbrc.skillcore.data.MobData;
import de.tomalbrc.skillcore.data.RandomSpawnData;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class SkillCoreRegistrationEvents {
    public static final Event<RegistrationEvent<ItemData>> ITEM = EventFactory.createArrayBacked(RegistrationEvent.class, (callbacks) -> (data) -> {
        for(RegistrationEvent<ItemData> callback : callbacks) {
            callback.registered(data);
        }
    });

    public static final Event<RegistrationEvent<MobData>> MOB = EventFactory.createArrayBacked(RegistrationEvent.class, (callbacks) -> (data) -> {
        for(RegistrationEvent<MobData> callback : callbacks) {
            callback.registered(data);
        }
    });

    public static final Event<RegistrationEvent<MetaSkill>> METASKILL = EventFactory.createArrayBacked(RegistrationEvent.class, (callbacks) -> (metaSkill) -> {
        for(RegistrationEvent<MetaSkill> callback : callbacks) {
            callback.registered(metaSkill);
        }
    });

    public static final Event<RegistrationEvent<RandomSpawnData>> SPAWNER = EventFactory.createArrayBacked(RegistrationEvent.class, (callbacks) -> (spawnData) -> {
        for(RegistrationEvent<RandomSpawnData> callback : callbacks) {
            callback.registered(spawnData);
        }
    });

    public static final Event<RegistrationEvent<DropTable>> DROP_TABLE = EventFactory.createArrayBacked(RegistrationEvent.class, (callbacks) -> (table) -> {
        for(RegistrationEvent<DropTable> callback : callbacks) {
            callback.registered(table);
        }
    });

    @FunctionalInterface
    public interface RegistrationEvent<T> {
        void registered(T obj);
    }

}
