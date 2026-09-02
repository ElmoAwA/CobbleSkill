package de.tomalbrc.skillcore.impl.gadget;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class GeyserGadget extends AbstractTickingGadget {
    final int maxHeight;
    final boolean lava;
    private int currentHeight = 0;

    private final Set<BlockPos> placedBlocks = new ConcurrentSkipListSet<>();

    public GeyserGadget(SkillTree tree, int maxHeight, int interval, int maxDuration, boolean lava) {
        super(tree, maxDuration, interval);
        this.maxHeight = maxHeight;
        this.lava = lava;
    }

    @Override
    public void onAsyncTick() {
        int phase = ticks;

        double halfDuration = maxLifetime / 2.0;
        double progress;

        if (phase <= halfDuration) {
            progress = (double) phase / halfDuration;
        } else {
            progress = (double) (maxLifetime - phase) / halfDuration;
        }

        int newHeight = (int) Math.round(maxHeight * progress);
        newHeight = Math.max(0, Math.min(maxHeight, newHeight));

        if (newHeight != currentHeight) {
            updateGeyser(newHeight);
            currentHeight = newHeight;
        }
    }

    private void updateGeyser(int newHeight) {
        Vec3 origin = tree().origin();
        BlockState fluidState = lava ? Blocks.LAVA.defaultBlockState() : Blocks.WATER.defaultBlockState();
        BlockState airState = Blocks.AIR.defaultBlockState();

        List<Packet<? super ClientGamePacketListener>> p = new ArrayList<>();
        for (int y = newHeight; y < currentHeight; y++) {
            BlockPos pos = BlockPos.containing(origin.x, origin.y + y, origin.z);
            p.add(new ClientboundBlockUpdatePacket(pos, airState));
            placedBlocks.remove(pos);
        }

        for (int y = currentHeight; y < newHeight; y++) {
            BlockPos pos = BlockPos.containing(origin.x, origin.y + y, origin.z);
            p.add(new ClientboundBlockUpdatePacket(pos, fluidState));
            placedBlocks.add(pos);
        }

        if (!p.isEmpty())
            sendPacket(new ClientboundBundlePacket(p));
    }

    @Override
    public void onHit(Entity entity) {
    }

    @Override
    public void onEnd() {
        for (BlockPos pos : placedBlocks) {
            this.sendPacket(new ClientboundBlockUpdatePacket(pos, initialTree.level().getBlockState(pos)));
        }

        placedBlocks.clear();
    }

    @Override
    public void sendPacket(Packet<? extends ClientGamePacketListener> packet) {
        var pos = initialTree.caster().position();
        for (ServerPlayer player : initialTree.level().players()) {
            if (player.distanceToSqr(pos) < 128*128)
                player.connection.send(packet);
        }
    }
}