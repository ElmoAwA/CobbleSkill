package de.tomalbrc.skillcore.mixin.accessor;

import de.tomalbrc.bil.core.holder.base.AbstractElementHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractElementHolder.class, remap = false)
public interface Haha {
    @Accessor
    void setElementsInitialized(boolean i);
}
