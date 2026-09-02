package de.tomalbrc.skillcore.mixin.trigger;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void sc$onPlayerDeathForEntity(DamageSource damageSource, CallbackInfo ci) {
        Entity source = damageSource.getEntity();
        if (source == null)
            source = this.getLastHurtByMob();

        if (source != null) {
            var o = source.overlay();
            if (o != null && o.getTriggerHandler() != null) {
                o.getTriggerHandler().onPlayerKill(this);
            }
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void sc$onPlayerDeath(DamageSource damageSource, CallbackInfo ci) {
        if (this.overlay() != null) {
            var o = this.playerOverlay();
            if (o != null && o.getTriggerHandler() != null) {
                o.getTriggerHandler().onPlayerKill(damageSource.getEntity());
            }
        }
    }
}
