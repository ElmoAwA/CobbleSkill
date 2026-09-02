package de.tomalbrc.skillcore.mixin.trigger;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {LivingEntity.class})
public abstract class CombatMixin extends Entity {
    @Shadow
    @Nullable
    private LivingEntity lastHurtByMob;

    @Shadow
    @Nullable
    private LivingEntity lastHurtMob;

    public CombatMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "onEnterCombat", at = @At("HEAD"))
    private void sc$onEnterCombat(CallbackInfo ci) {
        var t = lastHurtByMob;
        if (t == null) {
            var e = this.overlay();
            if (e != null) {
                e.getTriggerHandler().onEnterCombat(t);
            }
        }
    }

    @Inject(method = "onLeaveCombat", at = @At("HEAD"))
    private void sc$onDropCombat(CallbackInfo ci) {
        var t = lastHurtMob;
        if (t == null) {
            var e = this.overlay();
            if (e != null) {
                e.getTriggerHandler().onDropCombat(t);
            }
        }
    }
}
