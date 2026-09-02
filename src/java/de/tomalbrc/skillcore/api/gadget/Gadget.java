package de.tomalbrc.skillcore.api.gadget;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public interface Gadget {
    void asyncTick();

    void onHit(Entity entity);
    void onEnd();

    ServerLevel level();

    void destroy();
    boolean finished();
}
