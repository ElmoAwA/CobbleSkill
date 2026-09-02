package de.tomalbrc.skillcore.impl.mechanic.effect;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ParticleRingEffectMechanic extends ParticleEffectMechanic {
    @SerializedName(value = "radius", alternate = {"r"})
    protected Resolvable<Double> radius = Resolvable.literal(1.0);
    @SerializedName(value = "points", alternate = {"pts"})
    protected int points = 24;

    @Override
    protected List<Vec3> computePositions(SkillTree tree, Target target, double effX, double effV, double effZ) {
        Vec3 origin = (fromOrigin && tree.origin() != null) ? tree.origin() : target.getPosition();
        origin = origin.add(0.0, yOffset.resolve(tree), 0.0);

        double rad = radius.resolve(tree);
        double increment = 2.0 * Math.PI / points;

        List<Vec3> positions = new ArrayList<>(Math.max(1, points));
        Vec3 dir = Vec3.ZERO;
        boolean directional = this.directional.resolve(tree);
        if (directional) {
            dir = directionReversed.resolve(tree)
                    ? origin.subtract(direction.resolve(tree))
                    : direction.resolve(tree).subtract(origin).normalize();
        }

        for (int i = 0; i < points; i++) {
            double angle = increment * i;
            double x = Math.cos(angle) * rad;
            double z = Math.sin(angle) * rad;
            Vec3 ringPoint = origin.add(x, 0.0, z);

            int amt = localAmount(tree);
            for (int j = 0; j < amt; j++) {
                Vec3 p = ringPoint.add(randomOffset(effX), randomOffset(effV), randomOffset(effZ));

                if (fixedYaw.resolve(tree) != -1111 || fixedPitch.resolve(tree) != -1111) {
                    p = applyFixedRotation(origin, p, fixedYaw.resolve(tree), fixedPitch.resolve(tree));
                }

                if (directional) {
                    p = p.add(dir);
                }

                positions.add(p);
            }
        }

        return positions;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.PARTICLE_RING_EFFECT;
    }
}
