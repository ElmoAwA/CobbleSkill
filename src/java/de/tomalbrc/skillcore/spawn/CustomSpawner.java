package de.tomalbrc.skillcore.spawn;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.data.MobData;
import de.tomalbrc.skillcore.data.RandomSpawnData;
import de.tomalbrc.skillcore.registry.MobRegistry;
import de.tomalbrc.skillcore.registry.SpawnerRegistry;
import de.tomalbrc.skillcore.util.BukkitIdConverter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import xyz.nucleoid.stimuli.Stimuli;
import xyz.nucleoid.stimuli.event.entity.EntitySpawnEvent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CustomSpawner {
    public static Map<ResourceLocation, MobData> vanillaOverrides = new HashMap<>();

    public static void addOverride(MobData data) {
        var id = BukkitIdConverter.entityType(data.identifier()).orElseGet(() -> ResourceLocation.parse(data.identifier().toLowerCase(Locale.ROOT)));
        if (data.type() == null && BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
            vanillaOverrides.put(id, data);
        }
    }

    public static void registerEventHandler() {
        Stimuli.global().listen(EntitySpawnEvent.EVENT, CustomSpawner::onSpawn);
    }

    private static InteractionResult onSpawn(Entity entity) {
        if (entity.mobId() != null || entity instanceof Player)
            return InteractionResult.PASS;

        var mobPath = entity.getType().builtInRegistryHolder().key().location();
        var override = vanillaOverrides.get(mobPath);
        if (override != null) {
            entity.mobId(override.identifier());
            override.setup(entity);
            return InteractionResult.PASS;
        }

        for (RandomSpawnData datum : SpawnerRegistry.all().values()) {
            if (datum.chance() != null && Math.random() > datum.chance())
                continue;

            // TODO: biome, world-id check

            if (datum.action() == RandomSpawnData.Action.REPLACE) {
                var target = Target.of(entity);
                var tempTree = new SkillTree(entity, target, entity.position());
                var success = datum.conditions().stream().allMatch(x -> x.test(tempTree, target));
                if (!success)
                    continue;

                var customType = datum.type();
                if (customType != null) {
                    MobRegistry.getOptional(customType).ifPresent(x -> {
                        var replacement = x.spawn((ServerLevel) entity.level(), entity.position());
                        if (replacement != null) replacement.setUUID(entity.getUUID());
                        if (replacement != null && datum.level() != null) replacement.setCustomLevel(datum.level());
                    });
                } else if (datum.types() != null) {
                    // TODO: better random
                    var t = datum.types().get((int)((datum.types().size())*Math.random())-1);
                    MobRegistry.getOptional(t).ifPresent(x -> {
                        var replacement = x.spawn((ServerLevel) entity.level(), entity.position());
                        if (replacement != null) replacement.setUUID(entity.getUUID());
                        if (replacement != null && datum.level() != null) replacement.setCustomLevel(datum.level());
                    });
                }

                entity.discard();
                return InteractionResult.CONSUME;
            } else if (datum.action() == RandomSpawnData.Action.ADD) {
                // TODO: add
            } else if (datum.action() == RandomSpawnData.Action.DENY) {
                return InteractionResult.FAIL;
            } else if (datum.action() == RandomSpawnData.Action.UPGRADE) {
                var target = Target.of(entity);
                var tempTree = new SkillTree(entity, target, entity.position());

                var success = datum.conditions().stream().allMatch(x -> x.test(tempTree, target));
                if (success) {
                    var customType = datum.type();
                    if (customType != null) {
                        MobRegistry.getOptional(customType).ifPresent(x -> {
                            entity.mobId(x.identifier());
                            x.setup(entity);
                        });
                    } else if (datum.types() != null) {
                        // TODO: better random
                        var t = datum.types().get((int)((datum.types().size())*Math.random())-1);
                        MobRegistry.getOptional(t).ifPresent(x -> {
                            var replacement = x.spawn((ServerLevel) entity.level(), entity.position());
                            if (replacement != null && datum.level() != null) replacement.setCustomLevel(datum.level());
                        });
                    }

                    if (datum.level() != null) entity.setCustomLevel(datum.level());
                }

                return InteractionResult.PASS;
            }
        }

        return InteractionResult.PASS;
    }
}
