package de.tomalbrc.skillcore.api.overlay;

import de.tomalbrc.skillcore.data.MobData;
import de.tomalbrc.skillcore.impl.TriggerHandler;
import de.tomalbrc.skillcore.util.EntityRefTable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface SkillCoreEntityOverlay {
    TriggerHandler<? extends Entity> getTriggerHandler();
    EntityRefTable immunityTable();
    LivingEntity getTarget();
    List<ServerPlayer> getTracking();

    MobData getMobData();
}
