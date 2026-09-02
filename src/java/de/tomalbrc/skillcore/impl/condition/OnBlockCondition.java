package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.core.BlockPos;

public class OnBlockCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        if (!target.isEntity())
            return false;

        BlockPos pos = target.getEntity().getOnPos();
        return !tree.level().getBlockState(pos).isAir();
    }
}
