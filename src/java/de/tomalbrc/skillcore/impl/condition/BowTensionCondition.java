package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

// TODO
public class BowTensionCondition extends AbstractCondition {
    protected float min, max;

    public boolean test(SkillTree tree, Target target) {
        return false;
    }
}
