package de.tomalbrc.skillcore.mixin.accessor;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractArrow.class)
public interface AbstractArrowAccessor {
    @Accessor
    void setPickup(AbstractArrow.Pickup pickup);

    @Accessor
    void setLife(int life);

    @Invoker
    void invokeSetPierceLevel(byte l);
}
