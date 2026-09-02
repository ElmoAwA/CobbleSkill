package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TriggerBlockTypeCondition extends AbstractCondition {
    Block block;

    // TODO: impl properly

    public boolean test(SkillTree tree, Target target) {
        Object t = tree.vars().get("triggerBlock");
        if (!(t instanceof BlockState bs)) return false;
        return bs.getBlock() == block;
    }
}
