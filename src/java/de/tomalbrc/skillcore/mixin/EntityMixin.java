package de.tomalbrc.skillcore.mixin;

import de.tomalbrc.bil.core.holder.wrapper.Bone;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.overlay.EntityOverlay;
import de.tomalbrc.skillcore.api.overlay.PlayerOverlay;
import de.tomalbrc.skillcore.ext.ManagedEntity;
import de.tomalbrc.skillcore.impl.variable.Variable;
import de.tomalbrc.skillcore.registry.MobRegistry;
import de.tomalbrc.skillcore.util.TextUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(Entity.class)
public abstract class EntityMixin implements ManagedEntity {
    @Shadow public abstract void setCustomName(@Nullable Component component);
    @Shadow public abstract SynchedEntityData getEntityData();

    @Unique String sc$mobId = null;
    @Unique int sc$level = 1;
    @Unique String sc$faction = "";
    @Unique String sc$stance = "";
    @Unique double sc$power = 1;
    @Unique boolean sc$forceInvisible = false;
    @Unique int sc$globalCooldownExpiration = 0;
    @Unique Map<String, Variable> sc$vars = new ConcurrentHashMap<>();
    @Unique @Nullable EntityOverlay<? extends Entity> sc$overlay;

    @Unique @Nullable Bone sc$seat;

    @Override
    public @Nullable EntityOverlay<? extends Entity> overlay() {
        return sc$overlay;
    }

    @Override
    public void overlay(EntityOverlay<? extends Entity> overlay) {
        if (sc$overlay != null) throw new UnsupportedOperationException("Can not override overlay!");

        sc$overlay = overlay;
    }

    @Override
    public String mobId() {
        return sc$mobId;
    }

    @Override
    public void mobId(String id) {
        sc$mobId = id;
    }

    @Override
    public void setCustomLevel(int level) {
        this.sc$level = level;
        if (this.mobId() != null) {
            SkillCore.SERVER.execute(() -> {
                var mobData = MobRegistry.getOptional(mobId());
                if (mobData.isPresent() && (Object)this instanceof LivingEntity living) {
                    mobData.get().applyLevelModifier(living);
                }

                MobRegistry.getOptional(mobId()).ifPresent(x -> {
                    var name = TextUtil.formatText(x.display(), (Entity) (Object) this);
                    setCustomName(name);
                });
            });
        }
    }

    @Override
    public int getCustomLevel() {
        return sc$level;
    }

    @Override
    public void setFaction(String faction) {
        this.sc$faction = faction;
    }

    @Override
    public String getFaction() {
        return sc$faction;
    }

    @Override
    public void setStance(String stance) {
        this.sc$stance = stance;
    }

    @Override
    public String getStance() {
        return sc$stance;
    }

    @Override
    public void setPower(double power) {
        this.sc$power = power;
    }

    @Override
    public double getPower() {
        return sc$power;
    }

    @Override
    public void setForceInvisible(boolean force) {
        sc$forceInvisible = force;
    }

    @Override
    public boolean isForceInvisible() {
        return sc$forceInvisible;
    }

    @Override
    public boolean isOnGlobalCooldown() {
        return SkillCore.SERVER.getTickCount() < sc$globalCooldownExpiration;
    }

    @Override
    public void setGlobalCooldown(int cooldown) {
        sc$globalCooldownExpiration = SkillCore.SERVER.getTickCount() + cooldown;
    }

    @Override
    public Map<String, Variable> getVariables() {
        return sc$vars;
    }

    @Inject(method = "onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V", at = @At("TAIL"))
    private void sc$onSyncedDataUpdate(EntityDataAccessor<?> entityDataAccessor, CallbackInfo ci) {
        var o = this.overlay();
        if (o != null) {
            o.onSyncedDataUpdated(entityDataAccessor, getEntityData().get(entityDataAccessor));
        }
    }

    @Inject(method = "save", at = @At("RETURN"))
    private void sc$save(CompoundTag compoundTag, CallbackInfoReturnable<Boolean> cir) {
        if (mobId() != null)
            compoundTag.putString("MobId", mobId());

        if (mobId() != null || overlay() != null) {
            compoundTag.putInt("CustomLevel", getCustomLevel());
            compoundTag.putDouble("PowerLevel", getPower());
            compoundTag.putString("Stance", getStance());
            compoundTag.putString("Faction", getFaction());
            compoundTag.putBoolean("ForceInvisible", sc$forceInvisible);
            compoundTag.putInt("GlobalCooldownExpiration", sc$globalCooldownExpiration);

            if (sc$overlay != null) sc$overlay.save(compoundTag);
        }
    }

    @Inject(method = "load", at = @At("HEAD"))
    private void sc$load(CompoundTag compoundTag, CallbackInfo ci) {
        if (compoundTag.contains("MobId"))
            mobId(compoundTag.getString("MobId"));

        if (compoundTag.contains("Stance")) setStance(compoundTag.getString("Stance"));
        if (compoundTag.contains("Faction")) setFaction(compoundTag.getString("Faction"));

        if (mobId() != null) {
            var mobData = MobRegistry.get(mobId());
            if (mobData != null) {
                mobData.setup((Entity) (Object) this, true);
            }
        }

        if (compoundTag.contains("CustomLevel")) setCustomLevel(compoundTag.getInt("CustomLevel"));
        if (compoundTag.contains("PowerLevel")) setPower(compoundTag.getDouble("PowerLevel"));

        if (compoundTag.contains("ForceInvisible")) this.setForceInvisible(compoundTag.getBoolean("ForceInvisible"));
        if (compoundTag.contains("GlobalCooldownExpiration")) this.setGlobalCooldown(compoundTag.getInt("GlobalCooldown"));

        if (sc$overlay == null) {

            if (mobId() != null) {
                MobRegistry.getOptional(mobId()).ifPresent(x -> new EntityOverlay<>((Entity) (Object)this, x));
            } else if ((Object)this instanceof ServerPlayer player) {
                var ignored = new PlayerOverlay(player);
            }

            if (sc$overlay != null) {
                sc$overlay.load(compoundTag);
                sc$overlay.getTriggerHandler().onLoad();
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void sc$onTick(CallbackInfo ci) {
        if (sc$overlay != null)
            sc$overlay.tick();
    }

    public @Nullable Bone getVirtualSeat() {
        return sc$seat;
    }

    public void setVirtualSeat(Bone virtualSeat) {
        sc$seat = virtualSeat;
    }

}
