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

public class ParticleBoxMechanic extends ParticleEffectMechanic {
    @SerializedName(value = "radius", alternate = {"r"})
    protected Resolvable<Double> radius = Resolvable.literal(5.0);

    @Override
    protected List<Vec3> computePositions(SkillTree tree, Target target, double effX, double effV, double effZ) {
        Vec3 origin = fromOrigin && tree.origin() != null ? tree.origin() : target.getPosition();
        origin = origin.add(0, yOffset.resolve(tree), 0);

        int amt = Math.max(1, localAmount(tree));
        double r = Math.max(0.0, radius.resolve(tree));

        List<Vec3> positions = new ArrayList<>(amt);

        for (int i = 0; i < amt; i++) {
            int face = (int) (Math.random() * 6.0);

            double u = (Math.random() * 2.0 - 1.0) * r;
            double v = (Math.random() * 2.0 - 1.0) * r;

            double px = 0, py = 0, pz = 0;

            switch (face) {
                case 0:
                    px = r;
                    py = u;
                    pz = v;
                    break;
                case 1:
                    px = -r;
                    py = u;
                    pz = v;
                    break;
                case 2:
                    px = u;
                    py = r;
                    pz = v;
                    break;
                case 3:
                    px = u;
                    py = -r;
                    pz = v;
                    break;
                case 4:
                    px = u;
                    py = v;
                    pz = r;
                    break;
                default:
                    px = u;
                    py = v;
                    pz = -r;
                    break;
            }

            Vec3 p = origin.add(randomOffset(effX), randomOffset(effV), randomOffset(effZ));
            Vec3 pos = p.add(px, py, pz);

            if (fixedYaw.resolve(tree) != -1111. || fixedPitch.resolve(tree) != -1111.) {
                pos = applyFixedRotation(origin, pos, fixedYaw.resolve(tree), fixedPitch.resolve(tree));
            }

            positions.add(pos);
        }

        return positions;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.PARTICLE_BOX;
    }
}
