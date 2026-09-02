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

public class PullMechanic extends AbstractMechanic {
    @SerializedName(value = "velocity", alternate = {"v"})
    Resolvable<Double> velocity = Resolvable.literal(1.);

    @SerializedName(value = "toorigin", alternate = {"to"})
    boolean toOrigin = false;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                var e = target.getEntity();
                if (e != null) {
                    var v = velocity.resolve(tree, target);
                    var loc = toOrigin ? tree.origin() : tree.caster().position();
                    double distance = loc.distanceTo(e.position());
                    double modxz = distance * 0.5 * v;
                    double mody = distance * 0.34 * v;
                    mody = loc.y - target.getPosition().y != 0.0 ? mody * Math.abs(loc.y - target.getPosition().y) * 0.5 : mody;
                    Vec3 next = e.position().subtract(loc).normalize().scale(v);
                    next = next.multiply(-1.0 * modxz, -1.0 * mody, -1.0 * modxz);
                    if (next.length() > 4) {
                        next = next.normalize().scale(4);
                    }

                    e.push(next);
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.PULL;
    }
}