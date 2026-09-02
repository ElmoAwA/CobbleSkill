package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import de.tomalbrc.skillcore.util.RangedValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DistanceFromLocationCondition extends AbstractCondition {
    @SerializedName("x")
    double x = 0.0;
    @SerializedName("y")
    double y = 0.0;
    @SerializedName("z")
    double z = 0.0;
    @SerializedName(value = "world", alternate = {"w"})
    Resolvable<String> worldName;

    @SerializedName(value = "distance", alternate = {"d"})
    RangedValue distance;

    @Override
    public boolean test(SkillTree tree, Target target) {
        Entity e = target.getEntity();
        Vec3 pos = e.position();
        Level entityWorld = e.level();

        if (worldName != null) {
            String currentWorldName = entityWorld.dimension().location().toString();
            if (!worldName.resolve(tree, target).equals(currentWorldName)) {
                return false;
            }
        }

        double dx = pos.x - x;
        double dy = pos.y - y;
        double dz = pos.z - z;
        double actualDistance = Math.sqrt(dx*dx + dy*dy + dz*dz);

        return distance != null && distance.isWithin(actualDistance);
    }
}
