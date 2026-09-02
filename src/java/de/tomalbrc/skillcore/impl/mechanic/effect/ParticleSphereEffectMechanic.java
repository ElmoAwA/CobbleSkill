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


public class ParticleSphereEffectMechanic extends ParticleEffectMechanic {
    @SerializedName(value = "radius", alternate = {"r"})
    protected Resolvable<Double> radius = Resolvable.literal(1.0);

    @Override
    protected Integer localAmount(SkillTree tree) {
        return 1;
    }

    @Override
    protected List<Vec3> computePositions(SkillTree tree, Target target, double effX, double effV, double effZ) {
        var rad = radius.resolve(tree, target);

        if (rad <= 0.0) {
            return super.computePositions(tree, target, effX, effV, effZ);
        }

        Vec3 origin = (fromOrigin && tree.origin() != null) ? tree.origin() : target.getPosition();
        origin = origin.add(0, yOffset.resolve(tree), 0);

        int n = Math.max(1, amount.resolve(tree));
        List<Vec3> positions = new ArrayList<>(n);

        final double phi = Math.PI * (3.0 - Math.sqrt(5.0));

        if (n == 1) {
            Vec3 p = origin.add(rad, 0.0, 0.0);
            if (fixedYaw.resolve(tree) != -1111 || fixedPitch.resolve(tree) != -1111) {
                p = rotateAroundOrigin(origin, p, fixedYaw.resolve(tree), fixedPitch.resolve(tree));
            }
            positions.add(p);
            return positions;
        }

        for (int i = 0; i < n; i++) {
            double y = 1.0 - (2.0 * i) / (double) (n - 1);
            double rAtY = Math.sqrt(Math.max(0.0, 1.0 - y * y));
            double theta = phi * i;

            double x = Math.cos(theta) * rAtY;
            double z = Math.sin(theta) * rAtY;

            Vec3 p = origin.add(randomOffset(effX), randomOffset(effV), randomOffset(effZ)).add(x * rad, y * rad, z * rad);

            if (fixedYaw.resolve(tree) != -1111 || fixedPitch.resolve(tree) != -1111) {
                p = rotateAroundOrigin(origin, p, fixedYaw.resolve(tree), fixedPitch.resolve(tree));
            }

            positions.add(p);
        }

        return positions;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.PARTICLE_SPHERE;
    }

    private Vec3 rotateAroundOrigin(Vec3 origin, Vec3 pos, double yawDeg, double pitchDeg) {
        Vec3 offset = pos.subtract(origin);

        if (yawDeg != -1111) {
            double yawRad = Math.toRadians(yawDeg);
            double x = offset.x * Math.cos(yawRad) - offset.z * Math.sin(yawRad);
            double z = offset.x * Math.sin(yawRad) + offset.z * Math.cos(yawRad);
            offset = new Vec3(x, offset.y, z);
        }

        if (pitchDeg != -1111) {
            double pitchRad = Math.toRadians(pitchDeg);
            double y = offset.y * Math.cos(pitchRad) - offset.z * Math.sin(pitchRad);
            double z = offset.y * Math.sin(pitchRad) + offset.z * Math.cos(pitchRad);
            offset = new Vec3(offset.x, y, z);
        }

        return origin.add(offset);
    }
}
