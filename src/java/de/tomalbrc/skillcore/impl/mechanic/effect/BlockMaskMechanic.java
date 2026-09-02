package de.tomalbrc.skillcore.impl.mechanic.effect;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import de.tomalbrc.skillcore.impl.gadget.AbstractTickingGadget;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.*;

public class BlockMaskMechanic extends AbstractMechanic {
    @SerializedName(value = "material", alternate = {"mat", "m", "block", "b"})
    Resolvable<String> block = Resolvable.literal("gravel");

    @SerializedName(value = "radius", alternate = {"r"})
    Resolvable<Double> radius = Resolvable.literal(0.);

    @SerializedName(value = "radiusy", alternate = {"ry"})
    Resolvable<Double> radiusY = Resolvable.literal(0.);

    @SerializedName(value = "noise", alternate = {"n"})
    Resolvable<Double> noise = Resolvable.literal(0.);

    @SerializedName(value = "duration", alternate = {"d"})
    Resolvable<Double> duration = Resolvable.literal(0.);

    @SerializedName(value = "shape", alternate = {"s"})
    Resolvable<String> shape = Resolvable.literal("SPHERE");

    @SerializedName(value = "noair", alternate = {"na"})
    Resolvable<Boolean> noair = Resolvable.literal(true);

    @SerializedName(value = "onlyair", alternate = {"oa"})
    Resolvable<Boolean> onlyair = Resolvable.literal(false);

    @SerializedName(value = "occ", alternate = {"o", "oc"})
    Resolvable<Boolean> occ = Resolvable.literal(false);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        BlockState maskState;
        try {
            maskState = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), block.resolve(tree).toLowerCase(Locale.ROOT), false).blockState();
        } catch (CommandSyntaxException ignored) {
            return ExecutionResult.NULL;
        }

        double r = radius.resolve(tree);
        double ry = radiusY != null ? radiusY.resolve(tree) : r;
        double noiseVal = Math.max(0., Math.min(1., noise.resolve(tree)));
        boolean onlyAir = onlyair.resolve(tree);
        boolean noAir = noair.resolve(tree);
        boolean occludeTransparent = occ.resolve(tree);
        String shapeStr = shape.resolve(tree);
        boolean isSphere = !"CUBE".equalsIgnoreCase(shapeStr) && !"BOX".equalsIgnoreCase(shapeStr);

        Random random = new Random();

        int durTicks = (int) Math.max(0, Math.floor(duration.resolve(tree)));

        for (Target target : targets) {
            BlockPos origin = target.getBlockPos();
            Map<BlockPos, BlockState> originals = new HashMap<>();

            int ix = (int) Math.ceil(r);
            int iy = (int) Math.ceil(ry);
            int iz = (int) Math.ceil(r);

            double rSq = r * r;

            for (int dx = -ix; dx <= ix; dx++) {
                for (int dy = -iy; dy <= iy; dy++) {
                    for (int dz = -iz; dz <= iz; dz++) {
                        boolean inside;
                        if (isSphere) {
                            double ny = (ry == 0 ? dy : (dy * (r / ry)));
                            inside = (dx * dx + ny * ny + dz * dz) <= (rSq + 1e-6);
                        } else {
                            inside = true;
                        }
                        if (!inside) continue;

                        BlockPos pos = origin.offset(dx, dy, dz);
                        BlockState current = tree.level().getBlockState(pos);
                        boolean isAir = current.isAir();
                        boolean include;

                        if (onlyAir) {
                            if (occludeTransparent) {
                                include = isAir || !Shapes.block().equals(current.getShape(tree.level(), pos));
                            } else {
                                include = isAir;
                            }
                        } else if (noAir) {
                            include = !isAir;
                        } else {
                            include = true;
                        }

                        if (!include) continue;

                        if (noiseVal > 0.0 && random.nextDouble() < noiseVal) continue;

                        originals.put(pos, current);
                    }
                }
            }

            if (originals.isEmpty()) continue;

            List<Packet<? super ClientGamePacketListener>> maskPackets = new ArrayList<>(originals.size());
            for (BlockPos pos : originals.keySet()) {
                maskPackets.add(new ClientboundBlockUpdatePacket(pos, maskState));
            }
            ClientboundBundlePacket maskBundle = new ClientboundBundlePacket(maskPackets);

            for (ServerPlayer player : tree.caster().overlay().getTracking()) {
                player.connection.send(maskBundle);
            }

            if (durTicks > 0) {
                List<Packet<? super ClientGamePacketListener>> restorePackets = new ArrayList<>(originals.size());
                for (Map.Entry<BlockPos, BlockState> e : originals.entrySet()) {
                    restorePackets.add(new ClientboundBlockUpdatePacket(e.getKey(), e.getValue()));
                }

                var g = new BlockMaskGadget(tree, durTicks, 1, originals, maskState);
                SkillEngine.getInstance().addGadget(g);
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.BLOCKMASK;
    }

    private static class BlockMaskGadget extends AbstractTickingGadget {
        private final Map<BlockPos, BlockState> originals;
        private final ClientboundBundlePacket maskBundle;

        public BlockMaskGadget(SkillTree tree, int maxLifetime, int interval, Map<BlockPos, BlockState> originals, BlockState maskState) {
            super(tree, maxLifetime, interval);
            this.originals = new HashMap<>(originals);

            List<Packet<? super ClientGamePacketListener>> packets = new ArrayList<>(originals.size());
            for (BlockPos pos : originals.keySet()) {
                packets.add(new ClientboundBlockUpdatePacket(pos, maskState));
            }
            this.maskBundle = new ClientboundBundlePacket(packets);
        }

        @Override
        public void onAsyncTick() {
            if (ticks % 2 == 0) for (ServerPlayer player : tree().caster().overlay().getTracking()) {
                player.connection.send(maskBundle);
            }
        }

        @Override
        public void onHit(Entity entity) {
        }

        @Override
        public void onEnd() {
            List<Packet<? super ClientGamePacketListener>> restorePackets = new ArrayList<>(originals.size());
            for (BlockPos pos : originals.keySet()) {
                BlockState realState = tree().level().getBlockState(pos);
                restorePackets.add(new ClientboundBlockUpdatePacket(pos, realState));
            }
            ClientboundBundlePacket restoreBundle = new ClientboundBundlePacket(restorePackets);

            for (ServerPlayer player : tree().caster().overlay().getTracking()) {
                player.connection.send(restoreBundle);
            }
        }
    }
}
