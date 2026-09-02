package de.tomalbrc.skillcore.mixin.trigger;

import de.tomalbrc.skillcore.ext.ManagedEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin implements ManagedEntity {
    @Inject(method = "teleportTo(DDD)V", at = @At("HEAD"), cancellable = true)
    private void sc$onTeleport(double d, double e, double f, CallbackInfo ci) {
        var o = this.overlay();
        if (o != null && o.getTriggerHandler() != null) {
            InteractionResult res = o.getTriggerHandler().onTeleport();
            if (res.consumesAction()) ci.cancel();
        }
    }

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z", at = @At("HEAD"), cancellable = true)
    private void sc$onTeleport(ServerLevel serverLevel, double d, double e, double f, Set<RelativeMovement> set, float g, float h, CallbackInfoReturnable<Boolean> cir) {
        var o = this.overlay();
        if (o != null && o.getTriggerHandler() != null) {
            InteractionResult res = o.getTriggerHandler().onTeleport();
            if (res.consumesAction()) cir.setReturnValue(false);
        }
    }
}
