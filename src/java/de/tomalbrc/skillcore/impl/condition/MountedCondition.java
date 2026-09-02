package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

public class MountedCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        boolean isPassenger = target.getEntity().isPassenger();
        if (!isPassenger && target.getEntity().getVirtualSeat() != null) {
            return true;
        }

        return isPassenger;
    }
}
