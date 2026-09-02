package de.tomalbrc.skillcore.impl.gadget;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;

public class BloodyScreenGadget extends AbstractTickingGadget {
    ServerPlayer player;

    public BloodyScreenGadget(SkillTree tree, int maxDuration, int interval, ServerPlayer serverPlayer) {
        super(tree, maxDuration, interval);

        this.player = serverPlayer;
    }

    @Override
    public void onAsyncTick() {
        var border = new WorldBorder();
        border.setWarningBlocks((int)player.serverLevel().getWorldBorder().getSize()+128);
        this.player.connection.send(new ClientboundSetBorderWarningDistancePacket(border));
    }

    @Override
    public void onHit(Entity entity) {
    }

    @Override
    public void onEnd() {
        this.player.connection.send(new ClientboundSetBorderWarningDistancePacket(player.serverLevel().getWorldBorder()));
    }
}