package de.tomalbrc.skillcore.impl.mechanic.effect;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class BlockUnmaskMechanic extends AbstractMechanic {
    @SerializedName(value = "radius", alternate = {"r"})
    Resolvable<Double> radius = Resolvable.literal(0.);

    @SerializedName(value = "shape", alternate = {"s"})
    Resolvable<String> shape = Resolvable.literal("SPHERE");

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        double r = Math.max(0.0, radius.resolve(tree));
        String shapeStr = shape.resolve(tree);
        boolean isCube = "CUBE".equalsIgnoreCase(shapeStr) || "BOX".equalsIgnoreCase(shapeStr);

        int ir = (int) Math.ceil(r);
        double rSq = r * r;

        for (Target target : targets) {
            BlockPos origin = target.getBlockPos();
            List<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>();

            for (int dx = -ir; dx <= ir; dx++) {
                for (int dy = -ir; dy <= ir; dy++) {
                    for (int dz = -ir; dz <= ir; dz++) {
                        if (!isCube) {
                            double distSq = dx * dx + dy * dy + dz * dz;
                            if (distSq > rSq) continue;
                        }
                        BlockPos pos = origin.offset(dx, dy, dz);
                        BlockState current = tree.level().getBlockState(pos);

                        packets.add(new ClientboundBlockUpdatePacket(pos, current));
                    }
                }
            }

            if (packets.isEmpty()) continue;

            ClientboundBundlePacket bundle = new ClientboundBundlePacket(packets);
            for (ServerPlayer player : tree.caster().overlay().getTracking()) {
                player.connection.send(bundle);
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.BLOCKUNMASK;
    }
}
