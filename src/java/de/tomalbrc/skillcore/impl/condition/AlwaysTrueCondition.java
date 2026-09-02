package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

public class AlwaysTrueCondition extends AbstractCondition {
    @Override
    public boolean test(SkillTree tree, Target target) {
        return true;
    }
}
