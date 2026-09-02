package de.tomalbrc.skillcore.impl.target;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RectangleTargeter extends AbstractTargeter {
    @SerializedName(value = "x")
    Resolvable<Double> sizeX = Resolvable.literal(1.0);

    @SerializedName(value = "y")
    Resolvable<Double> sizeY = Resolvable.literal(1.0);

    @SerializedName(value = "z")
    Resolvable<Double> sizeZ = Resolvable.literal(1.0);

    @SerializedName(value = "xOffset", alternate = "ox")
    Resolvable<Double> offX = Resolvable.literal(0.0);

    @SerializedName(value = "yOffset", alternate = "oy")
    Resolvable<Double> offY = Resolvable.literal(0.0);

    @SerializedName(value = "zOffset", alternate = "oz")
    Resolvable<Double> offZ = Resolvable.literal(0.0);

    @SerializedName(value = "points", alternate = {"p", "density", "d"})
    Resolvable<Integer> points = Resolvable.literal(10);

    @SerializedName(value = "filled", alternate = {"fill", "f"})
    Resolvable<Boolean> filled = Resolvable.literal(false);

    @SerializedName(value = "outline", alternate = {"edge", "onlyedge", "e", "onlyoutline", "o"})
    Resolvable<Boolean> outline = Resolvable.literal(false);

    @SerializedName(value = "fromorigin", alternate = {"origin"})
    Resolvable<Boolean> fromOrigin = Resolvable.literal(false);

    @SerializedName(value = "rotation", alternate = {"r"})
    Resolvable<String> rotation = Resolvable.literal("0,0,0");

    @Override
    public List<Target> find(SkillTree tree) {
        var caster = tree.caster();

        double sx = sizeX.resolve(tree);
        double sy = sizeY.resolve(tree);
        double sz = sizeZ.resolve(tree);

        int pts = Math.max(2, points.resolve(tree));
        boolean isFilled = filled.resolve(tree);
        boolean isOutline = outline.resolve(tree);
        boolean isFromOrigin = fromOrigin.resolve(tree);

        double ox = offX.resolve(tree);
        double oy = offY.resolve(tree);
        double oz = offZ.resolve(tree);

        String rotStr = rotation.resolve(tree);
        double[] rotations = parseRotationString(rotStr);

        double rx = Math.toRadians(rotations[0]);
        double ry = Math.toRadians(rotations[1]);
        double rz = Math.toRadians(rotations[2]);

        double minX = isFromOrigin ? 0 : -sx / 2;
        double minY = isFromOrigin ? 0 : -sy / 2;
        double minZ = isFromOrigin ? 0 : -sz / 2;

        Vec3 origin = caster.position().add(ox, oy, oz);
        Set<Vec3> resultsVec = new HashSet<>();

        for (int i = 0; i < pts; i++) {
            double progX = (double) i / (pts - 1);
            double currentX = minX + (progX * sx);
            boolean isXEdge = (i == 0 || i == pts - 1);

            for (int j = 0; j < pts; j++) {
                double progY = (double) j / (pts - 1);
                double currentY = minY + (progY * sy);
                boolean isYEdge = (j == 0 || j == pts - 1);

                for (int k = 0; k < pts; k++) {
                    double progZ = (double) k / (pts - 1);
                    double currentZ = minZ + (progZ * sz);
                    boolean isZEdge = (k == 0 || k == pts - 1);

                    boolean shouldAdd;

                    if (isFilled) {
                        shouldAdd = true;
                    } else if (isOutline) {
                        int edgesHit = (isXEdge ? 1 : 0) + (isYEdge ? 1 : 0) + (isZEdge ? 1 : 0);
                        shouldAdd = (edgesHit >= 2);
                    } else {
                        shouldAdd = (isXEdge || isYEdge || isZEdge);
                    }

                    if (shouldAdd) {
                        Vec3 point = new Vec3(currentX, currentY, currentZ);
                        point = applyRotations(point, rx, ry, rz);
                        point = origin.add(point);
                        resultsVec.add(point);
                    }
                }
            }
        }

        return resultsVec.stream().map(x -> Target.of(tree.level(), x)).toList();
    }

    private double[] parseRotationString(String rotStr) {
        double[] result = new double[]{0.0, 0.0, 0.0};

        if (rotStr == null || rotStr.isEmpty()) {
            return result;
        }

        try {
            String[] parts = rotStr.split(",");
            if (parts.length >= 1) result[0] = Double.parseDouble(parts[0].trim());
            if (parts.length >= 2) result[1] = Double.parseDouble(parts[1].trim());
            if (parts.length >= 3) result[2] = Double.parseDouble(parts[2].trim());
        } catch (NumberFormatException ignored) {

        }

        return result;
    }

    private Vec3 applyRotations(Vec3 v, double rx, double ry, double rz) {
        if (rx != 0) v = rotateAroundX(v, rx);
        if (ry != 0) v = rotateAroundY(v, ry);
        if (rz != 0) v = rotateAroundZ(v, rz);
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