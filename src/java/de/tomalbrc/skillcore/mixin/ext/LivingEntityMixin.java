package de.tomalbrc.skillcore.mixin.ext;

import de.tomalbrc.skillcore.ext.EntityAsLiving;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements EntityAsLiving {
    @Override
    public LivingEntity asLivingEntity() {
        return (LivingEntity)(Object) this;
    }
}
