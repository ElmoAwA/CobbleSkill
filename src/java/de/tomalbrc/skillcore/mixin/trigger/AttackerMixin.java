package de.tomalbrc.skillcore.mixin.trigger;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(value = {LivingEntity.class, Mob.class})
public abstract class AttackerMixin extends Entity {
    public AttackerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "doHurtTarget", at = @At("HEAD"), cancellable = true)
    private void sc$onAttack(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (this.overlay() != null) {
            if (Objects.requireNonNull(this.overlay()).getTriggerHandler().onAttack((ServerLevel) entity.level(), entity).consumesAction())
                cir.setReturnValue(false);
        }
    }
}
