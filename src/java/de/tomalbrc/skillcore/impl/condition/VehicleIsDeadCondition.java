package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

public class VehicleIsDeadCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        return target.getEntity().getVehicle() != null && !target.getEntity().getVehicle().isAlive();
    }
}
