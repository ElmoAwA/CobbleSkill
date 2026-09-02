package de.tomalbrc.skillcore.mixin.options;

import de.tomalbrc.skillcore.data.MobData;
import de.tomalbrc.skillcore.registry.MobRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {Mob.class})
public abstract class SunburnMixin extends Entity {
    public SunburnMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    @Nullable
    public abstract LivingEntity getTarget();

    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void sc$sunburnOption(CallbackInfoReturnable<Boolean> cir) {
        MobData mobData;
        if (mobId() != null && (mobData = MobRegistry.get(mobId())) != null && mobData.options() != null) {
            if (mobData.options().preventSunburn())
                cir.setReturnValue(false);
        }
    }
}
