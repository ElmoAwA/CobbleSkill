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

public class ThrowMechanic extends AbstractMechanic {
    @SerializedName(value = "velocity", alternate = {"v"})
    Resolvable<Double> velocity = Resolvable.literal(1.0);

    @SerializedName(value = "velocityy", alternate = {"vy", "yv", "yvelocity"})
    Resolvable<Double> velocityY = Resolvable.literal(1.0);

    @SerializedName(value = "fromorigin", alternate = {"fo"})
    Resolvable<Boolean> fromOrigin = Resolvable.literal(false);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) {
            return ExecutionResult.NULL;
        }

        Entity caster = tree.caster();
        if (caster == null) {
            return ExecutionResult.NULL;
        }

        double vel = velocity.resolve(tree) / 10.0;
        double velY = velocityY.resolve(tree) / 10.0;
        boolean useOrigin = fromOrigin.resolve(tree);

        var pos = useOrigin ? tree.origin() : tree.caster().position();

        for (Target t : targets) {
            Entity target = t.getEntity();
            if (target == null) continue;

            Vec3 delta = t.getPosition().subtract(pos).normalize().scale(vel);

            delta = new Vec3(delta.x, vel == 0 ? velY : delta.y + velY, delta.z);

            if (delta.length() > 4) {
                delta = delta.normalize().scale(4.);
            }

            target.setDeltaMovement(delta);
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.THROW;
    }
}
