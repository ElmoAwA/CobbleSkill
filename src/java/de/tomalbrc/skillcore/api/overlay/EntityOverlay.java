package de.tomalbrc.skillcore.api.overlay;

import de.tomalbrc.skillcore.data.MobData;
import de.tomalbrc.skillcore.impl.TriggerHandler;
import de.tomalbrc.skillcore.impl.mechanic.model.holder.LivingEntityHolder;
import de.tomalbrc.skillcore.registry.Models;
import de.tomalbrc.skillcore.util.EntityRefTable;
import de.tomalbrc.skillcore.util.RangedBossBar;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityOverlay<T extends Entity> implements SkillCoreEntityOverlay {
    final Map<String, LivingEntityHolder<LivingEntity>> modelHolderByName = new ConcurrentHashMap<>();
    TriggerHandler<T> triggerHandler;
    final Map<String, Long> cooldowns = new ConcurrentHashMap<>();

    final @NotNull T entity;
    final @Nullable MobData mobData;

    EntityRefTable immunityTable;

    RangedBossBar bossEvent;
    Map<String, RangedBossBar> bossBars = new ConcurrentHashMap<>();

    public EntityOverlay(@NotNull T entity, @Nullable MobData mobData) {
        this.entity = entity;
        this.mobData = mobData;
        this.triggerHandler = this.createTriggerHandler();

        if (mobData != null) {
            if (mobData.modules() != null) {
                if (mobData.modules().threatTable() && entity instanceof LivingEntity living) living.setThreatTableEnabled(true);
                if (mobData.modules().immunityTable()) this.immunityTable = new EntityRefTable();
            }

            if (mobData.bossBar() != null) {
                this.bossEvent = mobData.bossBar().asServerBossEvent(entity);
            }

            if (mobData.skills() != null) mobData.skills().forEach(this.triggerHandler::add);
        }

        entity.overlay(this);
    }

    protected TriggerHandler<T> createTriggerHandler() {
        return new TriggerHandler<T>(this.entity);
    }

    @Override
    public List<ServerPlayer> getTracking() {
        return PlayerLookup.tracking(entity).stream().toList();
    }

    @Override
    public LivingEntity getTarget() {
        return this.entity instanceof Mob x ? x.getTarget() : null;
    }

    public void addCustomModel(LivingEntityHolder<LivingEntity> holder, String name, boolean attach, boolean invis) {
        modelHolderByName.put(name, holder);
        if (entity.asLivingEntity() != null) entity.asLivingEntity().setForceInvisible(invis);

        if (holder != null && attach) {
            EntityAttachment.ofTicking(holder, entity);
            holder.getAnimator().playAnimation("idle");
        }
    }

    public void removedCustomModel(String name) {
        if (name == null) {
            for (LivingEntityHolder<LivingEntity> old : modelHolderByName.values()) {
                if (old != null && old.getAttachment() != null)
                    old.destroy();
            }
            modelHolderByName.clear();
        } else {
            var old = modelHolderByName.remove(name);
            if (old != null && old.getAttachment() != null)
                old.destroy();
        }
    }

    public void save(CompoundTag compoundTag) {
        if (!modelHolderByName.isEmpty()) {
            CompoundTag modelsTag = new CompoundTag();
            for (var entry : modelHolderByName.entrySet()) {
                modelsTag.putBoolean(entry.getKey(), entry.getValue().stateMachineHandler() != null);
            }
            compoundTag.put("AppliedModelsV110", modelsTag);
        }
    }

    public void load(CompoundTag compoundTag) {
        if (compoundTag.contains("AppliedModelsV110") && entity.asLivingEntity() != null) {
            var modelsTag = compoundTag.getCompound("AppliedModelsV110");

            var keys = modelsTag.getAllKeys();
            for (String key : keys) {
                boolean val = modelsTag.getBoolean(key);
                var l = new LivingEntityHolder<>(entity.asLivingEntity(), Models.getModel(key));
                if (val)
                    l.setupStateMachine();
                addCustomModel(l, key, true, entity.isForceInvisible());
            }
        }
    }

    public LivingEntityHolder<LivingEntity> customModel(String name) {
        return name == null ? modelHolderByName.isEmpty() ? null : modelHolderByName.values().iterator().next() : modelHolderByName.get(name);
    }

    public Collection<LivingEntityHolder<LivingEntity>> customModels() {
        return modelHolderByName.values();
    }

    @Override
    public TriggerHandler<? extends Entity> getTriggerHandler() {
        return triggerHandler;
    }

    @Override
    public EntityRefTable immunityTable() {
        return immunityTable;
    }

    @Override
    public @Nullable MobData getMobData() {
        return mobData;
    }

    public void addBossBar(String name, RangedBossBar bossEvent) {
        this.bossBars.put(name, bossEvent);
    }

    public void removeBossBar(String name) {
        this.bossBars.remove(name);
    }

    public RangedBossBar getBossBar(String name) {
        return this.bossBars.get(name);
    }

    public void tick() {
        if (this.bossEvent != null) {
            this.bossEvent.tick((ServerLevel) entity.level());

            if (this.mobData != null)
                this.bossEvent.setName(this.mobData.bossBar().name(entity));

            this.bossEvent.setProgress(entity.asLivingEntity().getHealth()/ entity.asLivingEntity().getMaxHealth());
        }
        this.bossBars.forEach((x,k) -> k.tick((ServerLevel) entity.level()));

        this.triggerHandler.tick();
    }

    public void onDespawn() {
        onDespawn(true);
    }

    public void onDespawn(boolean trigger) {
        if (this.bossEvent != null)
            this.bossEvent.removeAllPlayers();

        if (trigger)
            this.triggerHandler.onDespawn();
    }

    public void onSignal(Entity sender, String signal) {
        this.triggerHandler.onSignal(sender, signal);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor, Object o) {
        for (LivingEntityHolder<LivingEntity> value : this.modelHolderByName.values()) {
            value.onSyncedDataUpdated(entityDataAccessor, o);
        }
    }

    public boolean isOnCooldown(String skillId) {
        if (!this.cooldowns.containsKey(skillId))
            return false;

        long now = entity.level().getServer().getTickCount();
        long expiry = this.cooldowns.getOrDefault(skillId, 0L);
        return expiry != 0 && expiry > now;
    }

    public void setCooldown(String skillId, int cooldownTicks) {
        long expiry = entity.level().getServer().getTickCount() + Math.max(0, cooldownTicks);
        this.cooldowns.put(skillId, expiry);
    }
}
