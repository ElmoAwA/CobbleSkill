package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

import java.util.Objects;

public class GlidingCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        return Objects.requireNonNull(target.getEntity().asLivingEntity()).isFallFlying();
    }
}
