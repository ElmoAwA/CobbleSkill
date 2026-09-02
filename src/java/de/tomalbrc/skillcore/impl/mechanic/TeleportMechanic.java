package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class TeleportMechanic extends AbstractMechanic {
    @SerializedName(value = "spreadh", alternate = {"sh", "r", "radius"})
    Resolvable<Double> spreadH = Resolvable.literal(0.0);

    @SerializedName(value = "spreadv", alternate = {"sv"})
    Resolvable<Double> spreadV = Resolvable.literal(0.0);

    @SerializedName(value = "preservepitch", alternate = {"pp"})
    Resolvable<Boolean> preservePitch = Resolvable.literal(true);

    @SerializedName(value = "preserveyaw", alternate = {"py"})
    Resolvable<Boolean> preserveYaw = Resolvable.literal(true);

    @SerializedName(value = "unsafe", alternate = {"us"})
    Resolvable<Boolean> unsafe = Resolvable.literal(false);

    private final transient Random random = new Random();

    @Override
    public ExecutionResult execute(SkillTree tree) {
        Entity caster = tree.caster();

        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        double sh = spreadH.resolve(tree);
        double sv = spreadV.resolve(tree);
        boolean isUnsafe = unsafe.resolve(tree);

        boolean keepPitch = preservePitch.resolve(tree);
        boolean keepYaw = preserveYaw.resolve(tree);

        Target t = targets.getFirst();
        Entity target = t.getEntity();
        if (target == null) return ExecutionResult.NULL;

        Level world = caster.level();

        float yaw  = keepYaw   ? caster.getYRot() : target.getYRot();
        float pitch = keepPitch ? caster.getXRot() : target.getXRot();

        while (true) {
            double dx = (random.nextDouble() * 2. - 1.) * sh;
            double dz = (random.nextDouble() * 2. - 1.) * sh;
            double dy = (random.nextDouble() * 2. - 1.) * sv;

            var vec = t.getPosition().add(dx, dy, dz);
            BlockPos pos = BlockPos.containing(vec);
            if (isUnsafe) {
                SkillCore.SERVER.execute(() -> caster.teleportTo(tree.level(), vec.x, vec.y, vec.z, Set.of(), yaw, pitch));
                return ExecutionResult.NULL;
            }

            if (isSafeLanding(world, pos)) {
                SkillCore.SERVER.execute(() -> caster.teleportTo(tree.level(), vec.x, vec.y, vec.z, Set.of(), yaw, pitch));
                return ExecutionResult.NULL;
            }
        }
    }

    private boolean isSafeLanding(Level level, BlockPos pos) {
        BlockPos head = pos.above();
        BlockPos below = pos.below();

        BlockState feetState = level.getBlockState(pos);
        BlockState headState = level.getBlockState(head);
        BlockState belowState = level.getBlockState(below);

        if (!feetState.getCollisionShape(level, pos).isEmpty()) return false;
        if (!headState.getCollisionShape(level, head).isEmpty()) return false;

        return !belowState.getCollisionShape(level, below).isEmpty();
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.TELEPORT;
    }
}