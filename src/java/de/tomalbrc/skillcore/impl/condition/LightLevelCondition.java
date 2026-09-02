package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;

public class LightLevelCondition extends AbstractCondition {
    @SerializedName(value = "level", alternate = {"l"})
    RangedValue level;

    public boolean test(SkillTree tree, Target target) {
        BlockPos pos = target.getBlockPos();
        int l = tree.level().getBrightness(LightLayer.BLOCK, pos);
        return level.isWithin(l);
    }
}
