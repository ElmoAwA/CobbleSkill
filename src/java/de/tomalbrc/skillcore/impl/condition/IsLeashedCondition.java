package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.entity.Leashable;

public class IsLeashedCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        return target.getEntity() instanceof Leashable leashable && leashable.isLeashed();
    }
}
