package de.tomalbrc.skillcore.ext;

import net.minecraft.world.entity.LivingEntity;

public interface EntityAsLiving {
    default LivingEntity asLivingEntity() {
        throw new UnsupportedOperationException();
    }
}
