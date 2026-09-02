package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import de.tomalbrc.skillcore.util.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LeapMechanic extends AbstractMechanic {
    @SerializedName(value = "velocity", alternate = {"v"})
    Resolvable<Double> velocity = Resolvable.literal(100.0);

    @SerializedName(value = "noise", alternate = {"n"})
    Resolvable<Double> noise = Resolvable.literal(0.0);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        Entity caster = tree.caster();
        if (caster == null) return ExecutionResult.NULL;

        for (Target t : targets) {
            Entity target = t.getEntity();
            if (target == null) continue;

            double v = Math.max(0.0001, velocity.resolve(tree, t)); // avoid div-by-zero
            double noiseAmt = Math.max(0.0, noise.resolve(tree, t));


            Vec3 vec = t.getPosition().subtract(caster.position());
            Double launchAngle = Util.launchAngle(caster.position(), t.getPosition(), v, vec.y, 20.);
            double distance = Math.sqrt(Math.pow(vec.x, 2.0F) + Math.pow(vec.z, 2.));
            if (distance != 0.) {
                if (launchAngle == null) {
                    launchAngle = Math.atan((40. * vec.y + Math.pow(v, 2.)) / (40. * vec.y + 2. * Math.pow(v, 2.)));
                }

                double hangtime = Util.hangtime(launchAngle, v, vec.y, 20.);
                vec = new Vec3(vec.x, Math.tan(launchAngle) * distance, vec.z).normalize();

                Vec3 noise = new Vec3(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
                noise = noise.scale(this.noise.resolve(tree, t) / 10.0F);
                vec = vec.add(noise);
                v = v + 1.2 * Math.pow(hangtime, 2.) + (Math.random() - 0.8) / 2.;
                vec = vec.scale(v / 20.);
                if (vec.length() > 4) {
                    vec = vec.normalize().scale(4);
                }

                caster.push(vec);
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.LEAP;
    }
}
