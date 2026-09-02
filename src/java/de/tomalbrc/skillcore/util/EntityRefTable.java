package de.tomalbrc.skillcore.util;

import de.tomalbrc.skillcore.SkillCore;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityRefTable {
    private final Map<UUID, Double> damagePerEntity = new HashMap<>();

    public <E extends LivingEntity> double add(E entity, double damage) {
        var ref = entity.getUUID();
        return damagePerEntity.merge(ref, damage, Double::sum);
    }

    public double add(UUID entity, double damage) {
        return damagePerEntity.merge(entity, damage, Double::sum);
    }

    public <E extends LivingEntity> void set(E entity, double damage) {
        var ref = entity.getUUID();
        damagePerEntity.put(ref, damage);
    }

    public <E extends LivingEntity> double get(E entity) {
        var ref = entity.getUUID();
        return damagePerEntity.getOrDefault(ref, 0.);
    }

    public void set(UUID ref, double damage) {
        damagePerEntity.put(ref, damage);
    }

    public double get(UUID ref) {
        return damagePerEntity.getOrDefault(ref, 0.);
    }

    public Collection<ServerPlayer> players() {
        return SkillCore.SERVER.getPlayerList().getPlayers().stream().filter(x -> damagePerEntity.containsKey(x.getUUID())).toList();
    }

    public UUID getHighestEntry() {
        return damagePerEntity.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public Entity getHighestEntry(ServerLevel level) {
        return damagePerEntity.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .map(level::getEntity)
                .orElse(null);
    }

    public Collection<Entity> entities(ServerLevel level) {
        return damagePerEntity.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .map(level::getEntity)
                .toList();
    }
}