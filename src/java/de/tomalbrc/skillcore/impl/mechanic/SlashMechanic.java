package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.condition.Condition;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import de.tomalbrc.skillcore.impl.MetaSkillRef;
import de.tomalbrc.skillcore.impl.gadget.AbstractTickingGadget;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class SlashMechanic extends AbstractMechanic {
    @SerializedName(value = "points", alternate = {"p"})
    Resolvable<Integer> points = Resolvable.literal(32);

    @SerializedName(value = "duration", alternate = {"d"})
    Resolvable<Integer> duration = Resolvable.literal(0);

    @SerializedName(value = "width", alternate = {"w"})
    Resolvable<Double> width = Resolvable.literal(1.0);

    @SerializedName(value = "height", alternate = {"h"})
    Resolvable<Double> height = Resolvable.literal(1.0);

    @SerializedName(value = "angle", alternate = {"a", "arc"})
    Resolvable<Double> angle = Resolvable.literal(180.0);

    @SerializedName(value = "xoffset", alternate = {"xo", "x"})
    Resolvable<Double> xOffset = Resolvable.literal(0.0);

    @SerializedName(value = "yoffset", alternate = {"yo", "y"})
    Resolvable<Double> yOffset = Resolvable.literal(0.0);

    @SerializedName(value = "zoffset", alternate = {"zo", "z"})
    Resolvable<Double> zOffset = Resolvable.literal(0.0);

    @SerializedName(value = "targetxoffset", alternate = {"txo", "tx"})
    Resolvable<Double> targetxOffset = Resolvable.literal(0.0);

    @SerializedName(value = "targetyoffset", alternate = {"tyo", "ty"})
    Resolvable<Double> targetyOffset = Resolvable.literal(0.0);

    @SerializedName(value = "targetzoffset", alternate = {"tzo", "tz"})
    Resolvable<Double> targetzOffset = Resolvable.literal(0.0);

    @SerializedName(value = "forwardoffset", alternate = {"foffset", "fo"})
    Resolvable<Double> forwardOffset = Resolvable.literal(0.0);

    @SerializedName(value = "pitch")
    Resolvable<Double> pitch = Resolvable.literal(0.0);

    @SerializedName(value = "yaw")
    Resolvable<Double> yaw = Resolvable.literal(0.0);

    @SerializedName(value = "roll")
    Resolvable<Double> roll = Resolvable.literal(0.0);

    @SerializedName(value = "radius", alternate = {"r"})
    Resolvable<Double> radius = Resolvable.literal(1.0);

    @SerializedName(value = "specificstep", alternate = {"ss"})
    Resolvable<Double> specificStep = Resolvable.nullable();

    @SerializedName(value = "onpointskill", alternate = {"onpoint","op"})
    MetaSkillRef onPoint = new MetaSkillRef();

    @SerializedName(value = "onendskill", alternate = {"onend","oe"})
    MetaSkillRef onEnd = new MetaSkillRef();

    @SerializedName(value = "onstartskill", alternate = {"onstart","os"})
    MetaSkillRef onStart = new MetaSkillRef();

    @SerializedName(value = "onhitentityskill", alternate = {"onhitentity","ohe","oh"})
    MetaSkillRef onHitEntity = new MetaSkillRef();

    @SerializedName(value = "rotation", alternate = {"rot"})
    List<String> rotationString = List.of("0","0","0");

    @SerializedName(value = "matchcasterdirection", alternate = {"matchdirection","mcd"})
    Resolvable<Boolean> matchCasterDirection = Resolvable.literal(true);

    @SerializedName(value = "directiontowardstarget", alternate = {"dtt"})
    Resolvable<Boolean> directionTowardsTarget = Resolvable.literal(false);

    @SerializedName(value = "fromorigin")
    Resolvable<Boolean> fromOrigin = Resolvable.literal(false);

    @SerializedName(value = "hitconditions", alternate = {"conditions", "cond", "c", "oc", "hc"})
    List<Condition> hitConditions = null;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        int parsedPoints = Math.max(1, Objects.requireNonNullElse(points.resolve(tree), 1));
        double parsedAngle = Math.max(0.0, Objects.requireNonNullElse(angle.resolve(tree), 0.0));
        double parsedRadius = Math.max(0.0, Objects.requireNonNullElse(radius.resolve(tree), 0.0));
        int finalDuration = Math.max(0, Objects.requireNonNullElse(duration.resolve(tree), 0));

        List<Target> currentTargets = tree.getCurrentTargets();
        if (currentTargets == null || currentTargets.isEmpty()) return ExecutionResult.NULL;

        List<Vec3> allPoints = new ArrayList<>();
        for (Target target : currentTargets) {
            allPoints.addAll(calculatePointsForTarget(tree, target, parsedPoints, parsedAngle));
        }

        Double spec = specificStep.resolve(tree);
        if (spec != null && spec > 0) {
            int idx = (int) Math.floor(spec);
            if (idx >= 0 && idx < allPoints.size()) {
                allPoints = List.of(allPoints.get(idx));
            } else {
                allPoints = Collections.emptyList();
            }
        }

        if (allPoints.isEmpty()) return ExecutionResult.NULL;

        if (finalDuration == 0) {
            HashSet<UUID> seen = new HashSet<>();
            int total = allPoints.size();
            for (int i = 0; i < total; i++) {
                double stage = total == 1 ? 0.0 : (double) i / (total - 1);
                processPoint(tree, allPoints.get(i), stage, parsedRadius, hitConditions, seen);
            }
        } else {
            List<List<Vec3>> partitions = splitList(allPoints, finalDuration);
            SkillTree frozen = tree.copy();
            SlashGadget runner = new SlashGadget(frozen, this, finalDuration, partitions, allPoints.size(), parsedRadius, hitConditions);
            SkillEngine.getInstance().addGadget(runner);
        }

        return ExecutionResult.NULL;
    }

    private void processPoint(SkillTree tree, Vec3 point, double stage, double radiusVal, List<Condition> conditions, HashSet<UUID> seenEntities) {
        if (stage == 0.0) {
            onStart.cast(() -> tree.copyWithOrigin(point));
        } else if (stage == 1.0) {
            onEnd.cast(() -> tree.copyWithOrigin(point));
        } else {
            onPoint.cast(() -> tree.copyWith(point, List.of(Target.of(tree.level(), point))));
        }

        if (!onHitEntity.isEmpty()) {
            ServerLevel level = tree.level();
            AABB box = new AABB(point.x - radiusVal, point.y - radiusVal, point.z - radiusVal,
                    point.x + radiusVal, point.y + radiusVal, point.z + radiusVal);

            List<Entity> candidates = level.getEntities(tree.caster(), box, e -> !seenEntities.contains(e.getUUID()));

            if (candidates.isEmpty()) return;

            List<Target> validTargets = new ArrayList<>();
            for (Entity e : candidates) {
                if (conditions != null && !conditions.isEmpty()) {
                    boolean pass = true;
                    Target t = Target.of(e);
                    for (Condition c : conditions) {
                        if (!c.testWithTrigger(tree, t)) {
                            pass = false;
                            break;
                        }
                    }
                    if (!pass) continue;
                }

                seenEntities.add(e.getUUID());
                validTargets.add(Target.of(e));
            }

            if (!validTargets.isEmpty()) {
                onHitEntity.cast(() -> tree.copyWith(point, validTargets));
            }
        }
    }

    private List<Vec3> calculatePointsForTarget(SkillTree tree, Target target, int parsedPoints, double parsedAngle) {
        Vec3 origin = fromOrigin.resolve(tree) ? tree.origin() : target.getPosition();
        Vec3 userRotation = parseRotation(tree);

        float originPitch = 0;
        float originYaw = 0;

        if (tree.caster() != null && matchCasterDirection.resolve(tree)) {
            originPitch = tree.caster().getXRot();
            originYaw = tree.caster().getYRot();
        } else if (directionTowardsTarget.resolve(tree)) {
            Vec3 targetOffsetVec = new Vec3(targetxOffset.resolve(tree), targetyOffset.resolve(tree), targetzOffset.resolve(tree));
            Vec3 targetPos = target.getPosition().add(targetOffsetVec);
            Vec3 directionVector = targetPos.subtract(origin);

            double yawCalc = Math.atan2(directionVector.z, directionVector.x);
            yawCalc = Math.toDegrees(yawCalc) - 90.0;
            yawCalc = (yawCalc + 360.0) % 360.0;

            double horizontalDistance = Math.sqrt(directionVector.x * directionVector.x + directionVector.z * directionVector.z);
            double pitchCalc = Math.atan2(-directionVector.y, horizontalDistance);
            pitchCalc = Math.toDegrees(pitchCalc);

            originPitch = (float) pitchCalc;
            originYaw = (float) yawCalc;
        }

        Vec3 baseDirection = calculateDirectionVector(originPitch, originYaw).normalize();
        double forward = forwardOffset.resolve(tree);

        List<Vec3> arcVectors = calculateArc(width.resolve(tree), height.resolve(tree), parsedPoints, parsedAngle);

        List<Vec3> transformed = new ArrayList<>(arcVectors.size());
        Vec3 finalOffset = new Vec3(xOffset.resolve(tree), yOffset.resolve(tree), zOffset.resolve(tree));

        // 5. Apply Transformations Pipeline
        for (Vec3 v : arcVectors) {
            // A. Rotate by User Rotation (X -> Y -> Z)
            // Vec3 rotation methods require angles in RADIANS.
            v = v.xRot((float) Math.toRadians(userRotation.x));
            v = v.yRot((float) Math.toRadians(-userRotation.y)); // NEGATE Y
            v = v.zRot((float) Math.toRadians(userRotation.z));

            v = v.xRot((float) Math.toRadians(originPitch));
            v = v.yRot((float) Math.toRadians(-originYaw)); // NEGATE Y

            Vec3 forwardVec = baseDirection.scale(forward);
            v = v.add(forwardVec);

            v = origin.add(v);
            v = v.add(finalOffset);
            transformed.add(v);
        }

        return transformed;
    }

    private List<Vec3> calculateArc(double width, double height, int parsedPoints, double parsedAngle) {
        List<Vec3> out = new ArrayList<>(parsedPoints);
        double angleStep = parsedAngle / Math.max(1, (parsedPoints - 1));
        double startingAngle = -parsedAngle / 2.0;

        for (int i = 0; i < parsedPoints; i++) {
            double currentAngle = Math.toRadians(startingAngle + i * angleStep);
            out.add(new Vec3(
                    width * Math.sin(currentAngle),
                    0.0,
                    height * Math.cos(currentAngle)
            ));
        }
        return out;
    }

    private Vec3 calculateDirectionVector(float pitch, float yaw) {
        float f = pitch * ((float)Math.PI / 180F);
        float f1 = -yaw * ((float)Math.PI / 180F);
        float f2 = (float) Math.cos(f1);
        float f3 = (float) Math.sin(f1);
        float f4 = (float) Math.cos(f);
        float f5 = (float) Math.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    private Vec3 rotateAroundAxisX(Vec3 v, double angle) {
        angle = Math.toRadians(angle);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double y = v.y * cos - v.z * sin;
        double z = v.y * sin + v.z * cos;
        return new Vec3(v.x, y, z);
    }

    private Vec3 rotateAroundAxisY(Vec3 v, double angle) {
        angle = -angle;
        angle = Math.toRadians(angle);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = v.x * cos + v.z * sin;
        double z = v.x * -sin + v.z * cos;
        return new Vec3(x, v.y, z);
    }

    private Vec3 rotateAroundAxisZ(Vec3 v, double angle) {
        angle = Math.toRadians(angle);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = v.x * cos - v.y * sin;
        double y = v.x * sin + v.y * cos;
        return new Vec3(x, y, v.z);
    }

    private Vec3 parseRotation(SkillTree tree) {
        double rx = 0, ry = 0, rz = 0;
        if (rotationString != null && rotationString.size() == 3) {
            try {
                rx = Double.parseDouble(rotationString.get(0).trim());
                ry = Double.parseDouble(rotationString.get(1).trim());
                rz = Double.parseDouble(rotationString.get(2).trim());
            } catch (NumberFormatException ignored) {}
        }

        double p = pitch.resolve(tree);
        double y = yaw.resolve(tree);
        double r = roll.resolve(tree);

        if (p != 0.0) rx = p;
        if (y != 0.0) ry = y;
        if (r != 0.0) rz = r;

        return new Vec3(rx, ry, rz);
    }

    private static <T> List<List<T>> splitList(List<T> list, int n) {
        if (n <= 0) return List.of(new ArrayList<>(list));

        List<List<T>> parts = new ArrayList<>();
        int size = list.size();
        int partSize = size / n;
        int remainder = size % n;

        for (int i = 0; i < n; i++) {
            int from = i * partSize + Math.min(i, remainder);
            int to = from + partSize + (i < remainder ? 1 : 0);
            parts.add(new ArrayList<>(list.subList(from, to)));
        }
        return parts;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SLASH;
    }

    public static class SlashGadget extends AbstractTickingGadget {
        private final SkillTree initialTree;
        private final List<List<Vec3>> partitions;
        private final int totalPoints;
        private final int[] startIndices;
        private final double radiusVal;
        private final List<Condition> conditions;
        private final HashSet<UUID> seen = new HashSet<>();
        private SlashMechanic mechanic;

        public SlashGadget(SkillTree tree, SlashMechanic mechanic, int lifetimeTicks, List<List<Vec3>> partitions, int totalPoints, double radiusVal, List<Condition> conditions) {
            super(tree, lifetimeTicks, 1);
            this.mechanic = mechanic;
            this.initialTree = tree;
            this.partitions = partitions;
            this.totalPoints = Math.max(0, totalPoints);
            this.radiusVal = radiusVal;
            this.conditions = conditions;

            this.startIndices = new int[partitions.size()];
            int acc = 0;
            for (int i = 0; i < partitions.size(); i++) {
                startIndices[i] = acc;
                acc += partitions.get(i).size();
            }
        }

        @Override
        public void onAsyncTick() {
            int idx = ticks;
            List<Vec3> part = idx < partitions.size() ? partitions.get(idx) : Collections.emptyList();
            int base = idx < startIndices.length ? startIndices[idx] : 0;

            for (int j = 0; j < part.size(); j++) {
                int globalIndex = base + j;
                double stage = totalPoints <= 1 ? 0.0 : (double) globalIndex / (totalPoints - 1);
                this.mechanic.processPoint(initialTree, part.get(j), stage, radiusVal, conditions, seen);
            }
        }

        @Override
        public void onHit(Entity entity) {}

        @Override
        public void onEnd() {
            this.mechanic = null;
        }
    }
}