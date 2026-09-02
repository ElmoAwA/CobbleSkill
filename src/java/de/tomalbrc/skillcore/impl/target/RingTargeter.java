package de.tomalbrc.skillcore.impl.target;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import de.tomalbrc.skillcore.util.RangedValue;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class RingTargeter extends AbstractTargeter {
    @SerializedName(value = "radius", alternate = {"r"})
    Resolvable<RangedValue> radius = Resolvable.literal(RangedValue.of(5.0));

    @SerializedName(value = "points", alternate = {"p"})
    Resolvable<RangedValue> points = Resolvable.literal(RangedValue.of(10));

    @SerializedName(value = "rotationx", alternate = {"rotx", "rx"})
    Resolvable<Double> rotX = Resolvable.literal(0.0);

    @SerializedName(value = "rotationy", alternate = {"roty", "ry"})
    Resolvable<Double> rotY = Resolvable.literal(0.0);

    @SerializedName(value = "rotationz", alternate = {"rotz", "rz"})
    Resolvable<Double> rotZ = Resolvable.literal(0.0);

    @SerializedName(value = "offsetx", alternate = {"offx", "ox"})
    Resolvable<Double> offX = Resolvable.literal(0.0);

    @SerializedName(value = "offsety", alternate = {"offy", "oy"})
    Resolvable<Double> offY = Resolvable.literal(0.0);

    @SerializedName(value = "offsetz", alternate = {"offz", "oz"})
    Resolvable<Double> offZ = Resolvable.literal(0.0);

    @SerializedName("relative")
    Resolvable<Boolean> relative = Resolvable.literal(false);

    @Override
    public List<Target> find(SkillTree tree) {
        var caster = tree.caster();

        int pts = points.resolve(tree).getAsInteger();

        double rx = rotX.resolve(tree);
        double ry = rotY.resolve(tree);
        double rz = rotZ.resolve(tree);

        double ox = offX.resolve(tree);
        double oy = offY.resolve(tree);
        double oz = offZ.resolve(tree);

        boolean rel = relative.resolve(tree);

        Vec3 origin = caster.position().add(ox, oy, oz);
        List<Target> results = new ArrayList<>();

        for (int i = 0; i < pts; i++) {
            double r = radius.resolve(tree).get();

            double angle = (2 * Math.PI * i) / pts;
            double x = Math.cos(angle) * r;
            double z = Math.sin(angle) * r;

            Vec3 point = applyRotations(new Vec3(x, 0, z), rx, ry, rz);

            if (rel) {
                point = rotateAroundY(point, Math.toRadians(caster.getYRot()));
            }

            point = origin.add(point);
            results.add(Target.of(tree.level(), point));
        }

        return results;
    }

    private Vec3 applyRotations(Vec3 v, double rx, double ry, double rz) {
        v = rotateAroundX(v, rx);
        v = rotateAroundY(v, ry);
        v = rotateAroundZ(v, rz);
        return v;
    }

    private Vec3 rotateAroundX(Vec3 v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vec3(v.x, v.y * cos - v.z * sin, v.y * sin + v.z * cos);
    }

    private Vec3 rotateAroundY(Vec3 v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vec3(v.x * cos + v.z * sin, v.y, -v.x * sin + v.z * cos);
    }

    private Vec3 rotateAroundZ(Vec3 v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vec3(v.x * cos - v.y * sin, v.x * sin + v.y * cos, v.z);
    }
}
