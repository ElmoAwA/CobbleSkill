package de.tomalbrc.skillcore.mixin;

import de.tomalbrc.skillcore.ext.ManagedEntity;
import de.tomalbrc.skillcore.registry.MobRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class PreventLootLivingEntityMixin implements ManagedEntity {
    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"), cancellable = true)
    private void sc$onDropAllDeathLoot(ServerLevel serverLevel, DamageSource damageSource, CallbackInfo ci) {
        if (mobId() != null) {
            MobRegistry.getOptional(mobId()).ifPresent(x -> {
                if (x.options() != null && x.options().preventOtherDrops()) ci.cancel();

                x.lootDrop((Entity)(Object)this, damageSource.getEntity());
            });
        }

        var e = damageSource.getEntity();
        if (e != null) {
            MobRegistry.getOptional(e.mobId()).ifPresent(x -> {
                if (x.options() != null && x.options().preventMobKillDrops()) ci.cancel();
            });
        }
    }
}
