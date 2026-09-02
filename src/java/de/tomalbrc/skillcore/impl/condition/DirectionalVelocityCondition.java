package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;
import net.minecraft.world.phys.Vec3;

public class DirectionalVelocityCondition extends AbstractCondition {
    @SerializedName(value = "x", alternate = {"s", "side"})
    RangedValue x;
    @SerializedName(value = "absx", alternate = {"ax", "abss", "as"})
    Boolean absx = false;
    @SerializedName(value = "y", alternate = {"up", "down", "vertical", "v"})
    RangedValue y;
    @SerializedName(value = "absy", alternate = {"ay"})
    Boolean absy = false;
    @SerializedName(value = "z", alternate = {"f", "forward"})
    RangedValue z;
    @SerializedName(value = "absz", alternate = {"az", "absf", "af"})
    Boolean absz = false;

    @SerializedName(value = "relative", alternate = {"rel"})
    boolean relative;

    public boolean test(SkillTree tree, Target target) {
        Vec3 vel = target.getEntity().getDeltaMovement();

        double vx = vel.x;
        double vy = vel.y;
        double vz = vel.z;

        if (relative) {
            Vec3 forward = target.getEntity().getForward();

            Vec3 up = new Vec3(0.0, 1.0, 0.0);
            Vec3 right = forward.cross(up);
            double rightLen = Math.sqrt(right.x * right.x + right.y * right.y + right.z * right.z);
            if (rightLen < 1e-8) {
                float yawDeg = target.getEntity().getYRot();
                double yawRad = Math.toRadians(yawDeg);
                right = new Vec3(Math.cos(yawRad), 0.0, Math.sin(yawRad)).normalize();
            } else {
                right = right.normalize();
            }

            double forwardComp = vx * forward.x + vy * forward.y + vz * forward.z;
            double sideComp = vx * right.x + vy * right.y + vz * right.z;

            vx = forwardComp;
            vz = sideComp;
        }

        boolean res = true;
        if (x != null) res = x.isWithin(absx ? Math.abs(vx) : vx);
        if (y != null) res = res && y.isWithin(absy ? Math.abs(vy) : vy);
        if (z != null) res = res && z.isWithin(absz ? Math.abs(vz) : vz);

        return res;
    }
}