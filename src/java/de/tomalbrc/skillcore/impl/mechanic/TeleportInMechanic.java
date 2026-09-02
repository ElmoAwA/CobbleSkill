package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Set;

public class TeleportInMechanic extends AbstractMechanic {
    // x,y,z (forward/back, up/down, left/right)
    @SerializedName(value = "vector", alternate = {"direction","dir","d","v"})
    Resolvable<String> vector = Resolvable.literal("0,0,0");

    @SerializedName(value = "yaw", alternate = {"y"})
    Resolvable<Double> yawModifier = Resolvable.literal(0.0);

    @SerializedName(value = "targetasorigin", alternate = {"tao"})
    Resolvable<Boolean> targetAsOrigin = Resolvable.literal(false);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        Entity caster = tree.caster();

        String raw = vector.resolve(tree);
        String[] parts = raw.split(",");
        if (parts.length != 3) return ExecutionResult.NULL;

        double vx = parse(parts[0].trim());
        double vy = parse(parts[1].trim());
        double vz = parse(parts[2].trim());

        double yawAdd = yawModifier.resolve(tree);
        float casterYaw = caster.getYRot() + (float) yawAdd;

        double rad = Math.toRadians(casterYaw);

        double rx = vx * Math.cos(rad) - vz * Math.sin(rad);
        double rz = vx * Math.sin(rad) + vz * Math.cos(rad);

        for (Target t : targets) {
            Entity target = t.getEntity();
            if (target == null) continue;

            Entity origin = targetAsOrigin.resolve(tree) ? target : caster;

            double ox = origin.getX();
            double oy = origin.getY();
            double oz = origin.getZ();

            double tx = ox + rx;
            double ty = oy + vy;
            double tz = oz + rz;

            float yaw = target.getYRot();
            float pitch = target.getXRot();

            target.teleportTo(
                    tree.level(),
                    tx, ty, tz,
                    Set.of(),
                    yaw, pitch
            );
        }

        return ExecutionResult.NULL;
    }

    private double parse(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.TELEPORT_IN;
    }
}