package de.tomalbrc.skillcore.util;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;

@SuppressWarnings("unused")
public class RangedBossBar extends ServerBossEvent {
    private Entity centerEntity;

    private double range = 30.0;
    private double rangeSq = range * range;

    public RangedBossBar(Component name, BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay,
                         Entity centerEntity, double range) {
        super(name, color, overlay);
        this.centerEntity = centerEntity;
        this.setRange(range);
    }

    public void setCenterEntity(Entity center) {
        this.centerEntity = center;
    }

    public Entity getCenterEntity() {
        return this.centerEntity;
    }

    public void setRange(double range) {
        this.range = Math.max(0.0, range);
        this.rangeSq = this.range * this.range;
    }

    public double getRange() {
        return this.range;
    }

    public void tick(ServerLevel level) {
        if (this.centerEntity == null) {
            return;
        }

        if (this.centerEntity.isRemoved()) {
            this.removeAllPlayers();
            this.setVisible(false);
            return;
        }

        for (ServerPlayer player : level.players()) {
            double distSq = player.distanceToSqr(centerEntity);

            if (distSq <= this.rangeSq || player.level() != level) {
                this.addPlayer(player);
            } else {
                this.removePlayer(player);
            }
        }
    }
}
