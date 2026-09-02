package de.tomalbrc.skillcore.mixin.trigger;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public abstract class CreeperMixin extends Entity {
    public CreeperMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "explodeCreeper", at = @At("HEAD"), cancellable = true)
    private void sc$onExplode(CallbackInfo ci) {
        var o = this.overlay();
        if (o != null && o.getTriggerHandler() != null) {
            InteractionResult res = o.getTriggerHandler().onExplode();
            if (res.consumesAction()) ci.cancel();
        }
    }

    @Inject(method = "setSwellDir", at = @At("HEAD"), cancellable = true)
    private void sc$onPrime(CallbackInfo ci) {
        var o = this.overlay();
        if (o != null && o.getTriggerHandler() != null) {
            InteractionResult res = o.getTriggerHandler().onPrime();
            if (res.consumesAction()) ci.cancel();
        }
    }

    @Inject(method = "setSwellDir", at = @At("HEAD"), cancellable = true)
    private void sc$onCreeperCharge(CallbackInfo ci) {
        var o = this.overlay();
        if (o != null && o.getTriggerHandler() != null) {
            InteractionResult res = o.getTriggerHandler().onCreeperCharge();
            if (res.consumesAction()) ci.cancel();
        }
    }
}
