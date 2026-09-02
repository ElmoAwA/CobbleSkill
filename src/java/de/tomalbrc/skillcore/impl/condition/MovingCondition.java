package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

public class MovingCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        if (!target.isEntity())
            return false;

        return target.getEntity().getDeltaMovement().lengthSqr() > 0.001;
    }
}
