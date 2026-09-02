package de.tomalbrc.skillcore.mixin.trigger;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin extends Entity {
    public MobMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void sc$onChangeTarget(LivingEntity livingEntity, CallbackInfo ci) {
        var o = this.overlay();
        if (o != null && o.getTriggerHandler() != null) {
            InteractionResult res = o.getTriggerHandler().onChangeTarget(livingEntity);
            if (res.consumesAction()) ci.cancel();
        }
    }
}
