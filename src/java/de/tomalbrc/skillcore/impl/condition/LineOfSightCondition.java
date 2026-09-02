package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

import java.util.Objects;

public class LineOfSightCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        if (tree.caster().asLivingEntity() == null || target.getEntity() == null)
            return false;

        return Objects.requireNonNull(tree.caster().asLivingEntity()).hasLineOfSight(target.getEntity());
    }
}
