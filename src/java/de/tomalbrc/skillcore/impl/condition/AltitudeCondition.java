package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;
import net.minecraft.core.BlockPos;

public class AltitudeCondition extends AbstractCondition {
    @SerializedName(value = "height", alternate = {"altitude", "a", "h"})
    RangedValue heightRange;

    @SerializedName(value = "maxheight", alternate = {"mh"})
    double maxHeight = 30;

    @Override
    public boolean test(SkillTree tree, Target target) {
        BlockPos pos = target.getBlockPos();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos().set(pos);

        while (cursor.getY() > tree.level().getMinBuildHeight() && tree.level().isEmptyBlock(cursor.below())) {
            cursor.move(0, -1, 0);
        }

        int groundY = cursor.getY();
        double altitude = pos.getY() - groundY;

        if (!heightRange.isWithin(altitude)) return false;
        return altitude <= maxHeight;
    }
}
