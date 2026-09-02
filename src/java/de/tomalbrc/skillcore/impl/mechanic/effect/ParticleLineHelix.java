package de.tomalbrc.skillcore.impl.mechanic.effect;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ParticleLineHelix extends ParticleEffectMechanic {
    @SerializedName(value = "distancebetween", alternate = {"db"})
    protected float distanceBetween = 1.0f;
    @SerializedName(value = "startyoffset", alternate = {"syo", "ystartoffset"})
    protected float startYOffset = 0.0f;
    @SerializedName(value = "targetyoffset", alternate = {"tyo", "ytargetoffset"})
    protected float targetYOffset = 0.0f;
    @SerializedName(value = "helixlength", alternate = {"hl"})
    protected double helixLength = 2.0;
    @SerializedName(value = "helixradius", alternate = {"hr"})
    protected double helixRadius = 1.0;
    @SerializedName(value = "helixrotation", alternate = {"rot"})
    protected double helixRotation = 0.0;
    @SerializedName(value = "maxdistance", alternate = {"md"})
    protected double maxDistance = 256.0;

    @Override
    protected List<Vec3> computePositions(SkillTree tree, Target target, double effX, double effV, double effZ) {
        Vec3 start;
        if (fromOrigin && tree.origin() != null) {
            start = tree.origin().add(0.0, startYOffset, 0.0);
        } else if (tree.caster() != null) {
            start = tree.caster().position().add(0.0, startYOffset, 0.0);
        } else {
            start = target.getPosition().add(0.0, startYOffset, 0.0);
        }

        Vec3 end = target.getPosition().add(0.0, targetYOffset, 0.0);

        Vec3 direction = end.subtract(start);
        double distance = Math.min(direction.length(), maxDistance);
        Vec3 dirUnit = direction.normalize();

        double revolutions = distance / helixLength;
        double particlesPerRevolution = helixLength / distanceBetween;
        int totalParticles = Math.max(1, (int)Math.round(particlesPerRevolution * revolutions));
        double step = distance / (double) totalParticles;

        Vec3 up;
        if (Math.abs(dirUnit.y) < 0.99) {
            up = new Vec3(0.0, 1.0, 0.0);
        } else {
            up = new Vec3(1.0, 0.0, 0.0);
        }

        Vec3 side = dirUnit.cross(up).normalize();
        double helixRotationRad = Math.toRadians(helixRotation);

        List<Vec3> positions = new ArrayList<>(totalParticles);
        for (int i = 0; i < totalParticles; i++) {
            double angle = 2.0 * Math.PI * ((double) i / particlesPerRevolution) + helixRotationRad;
            double xOffset = helixRadius * Math.cos(angle);
            double zOffset = helixRadius * Math.sin(angle);
            Vec3 offset = side.scale(xOffset).add(up.scale(zOffset));
            Vec3 particlePos = start.add(dirUnit.scale(step * i)).add(offset);
            positions.add(particlePos);
        }

        return positions;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.PARTICLE_LINE_HELIX;
    }
}
