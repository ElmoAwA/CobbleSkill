package de.tomalbrc.skillcore.core;

import de.tomalbrc.skillcore.impl.aura.AbstractAura;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuraManager {
    private final Map<Entity, List<AbstractAura<?>>> active = new ConcurrentHashMap<>();

    public void add(AbstractAura<?> aura) {
        active.computeIfAbsent(aura.target.getEntity(), e -> Collections.synchronizedList(new ArrayList<>())).add(aura);

        aura.createBossBar();
        aura.onStart();
    }

    public void asyncTick() {
        for (var entry : active.entrySet()) {
            List<AbstractAura<?>> list = entry.getValue();

            synchronized (list) {
                list.removeIf(a -> {
                    boolean dead = a.asyncTick();
                    if (dead) a.onEnd(a.mechanic.runEndSkillOnTerminate);
                    return dead;
                });
            }
        }
    }

    public void clearAuras(Entity entity, boolean runEndSkill) {
        List<AbstractAura<?>> list = active.remove(entity);
        if (list != null) {
            for (AbstractAura<?> a : list) {
                a.onEnd(runEndSkill);
            }
        }
    }

    public void remove(Entity entity, String s) {
        var list = active.get(entity);
        if (list != null && !list.isEmpty()) list.removeIf(x -> x.auraName.equals(s));
    }

    public boolean has(Entity entity, String s) {
        var list = active.get(entity);
        if (list != null && !list.isEmpty()) for (AbstractAura<?> aura : list) {
            if (s.equals(aura.auraName))
                return true;
        }

        return false;
    }

    public void onDespawn(Entity entity, ServerLevel serverLevel) {
        active.entrySet().removeIf(x -> x.getKey() == entity);
    }

    public @Nullable List<AbstractAura<?>> get(Entity key) {
        return active.get(key);
    }

    public void onDeath(@NotNull Entity key) {
        for (Map.Entry<Entity, List<AbstractAura<?>>> entry : active.entrySet()) {
            var entity = entry.getKey();
            var auras = entry.getValue();

            auras.forEach(aura -> {
                if (entity == key && aura.mechanic.cancelOnDeath)
                    aura.cancel();
                if (entity == aura.caster && aura.mechanic.cancelOnCasterDeath)
                    aura.cancel();
            });
        }
    }
}
