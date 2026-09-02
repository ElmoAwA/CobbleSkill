package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class DistanceFromSpawnCondition extends AbstractCondition {
    @SerializedName(value = "distance", alternate = "d")
    RangedValue distance;

    public boolean test(SkillTree tree, Target target){
        BlockPos spawn = tree.level().getSharedSpawnPos();
        double d = new Vec3(spawn.getX(), spawn.getY(), spawn.getZ()).distanceToSqr(target.getPosition());
        return distance.isWithin(d);
    }
}