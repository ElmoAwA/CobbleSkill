package de.tomalbrc.skillcore.mixin.ext;

import de.tomalbrc.skillcore.ext.EntityAsLiving;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixin implements EntityAsLiving {
    @Override
    public LivingEntity asLivingEntity() {
        return null;
    }
}
