package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LungeMechanic extends AbstractMechanic {
    @SerializedName(value = "velocity", alternate = {"v", "magnitude"})
    Resolvable<Double> velocity = Resolvable.literal(1.0);

    @SerializedName(value = "velocityy", alternate = {"vy", "yv", "yvelocity"})
    Resolvable<Double> velocityY = Resolvable.literal(0.0);

    @SerializedName(value = "oldmath", alternate = {"old", "o"})
    Resolvable<Boolean> oldmath = Resolvable.literal(false);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        for (Target t : targets) {
            double velocity = Math.abs(this.velocity.resolve(tree, t));
            double velocityY = this.velocityY.resolve(tree, t);
            // TODO: use caster direction vec * 2 if delta length == 0

            Vec3 v = t.getPosition().subtract(tree.caster().position()).normalize().multiply(1., (oldmath.resolve(tree, t) ? 1. : 0.), 1.);
            v = v.scale(velocity);
            if (velocityY != 0) {
                v = new Vec3(v.x, velocityY, v.z);
            }

            if (v.length() > 4) {
                v = v.normalize().scale(4);
            }

            tree.caster().setDeltaMovement(v);
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.LUNGE;
    }
}