package de.tomalbrc.skillcore.mixin.trigger;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TamableAnimal.class)
public abstract class TamableAnimalMixin extends Entity {
    public TamableAnimalMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tame", at = @At("HEAD"), cancellable = true)
    private void sc$onTame(Player player, CallbackInfo ci) {
        if (this.overlay() != null && this.overlay().getTriggerHandler() != null) {
            InteractionResult res = this.overlay().getTriggerHandler().onTame(player);
            if (res.consumesAction())
                ci.cancel();
        }
    }
}
