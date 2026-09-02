package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class JumpMechanic extends AbstractMechanic {
    @SerializedName(value = "velocity", alternate = {"v"})
    Resolvable<Double> velocity = Resolvable.literal(1.0);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        double vel = velocity.resolve(tree);

        for (Target t : targets) {
            Entity e = t.getEntity();
            if (e == null) continue;

            Vec3 current = e.getDeltaMovement();
            Vec3 next = new Vec3(current.x, current.y + vel, current.z);
            if (next.length() > 4) {
                next = next.normalize().scale(4);
            }

            e.setDeltaMovement(next);
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.JUMP;
    }
}
