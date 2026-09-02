package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

public class IsInvulnerableCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        return target.isEntity() && target.getEntity().isInvulnerable();
    }
}
