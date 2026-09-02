package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.core.BlockPos;

public class InsideCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        BlockPos pos = BlockPos.containing(target.getEntity().getX(), target.getEntity().getEyeY() + 0.5, target.getEntity().getZ());
        return !tree.level().canSeeSky(pos);
    }
}
