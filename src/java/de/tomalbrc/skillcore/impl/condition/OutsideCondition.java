package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.core.BlockPos;

public class OutsideCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target){
        BlockPos pos = target.getBlockPos();
        return tree.level().canSeeSky(pos);
    }
}
