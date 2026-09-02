package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

public class DawnCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        long time = tree.level().getDayTime() % 24000;
        return time >= 22000 || time <= 2000;
    }
}
