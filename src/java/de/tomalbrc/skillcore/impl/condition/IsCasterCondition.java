package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

public class IsCasterCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        return tree.caster().equals(target.getEntity());
    }
}
