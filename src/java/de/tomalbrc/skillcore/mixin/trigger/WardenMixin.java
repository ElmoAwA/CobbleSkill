package de.tomalbrc.skillcore.mixin.trigger;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/world/entity/monster/warden/Warden$VibrationUser")
public class WardenMixin {
    @Inject(method = "onReceiveVibration", at = @At("HEAD"), cancellable = true)
    private void sc$onHear(ServerLevel serverLevel, BlockPos blockPos, Holder<GameEvent> holder, Entity entity, Entity entity2, float f, CallbackInfo ci) {
        var e = entity2 != null ? entity2 : entity;
        var o = e.overlay();
        if (o != null) {
            InteractionResult res = o.getTriggerHandler().onHear(e);
            if (res.consumesAction())
                ci.cancel();
        }
    }
}
