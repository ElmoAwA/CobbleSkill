package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

public class SameFactionCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        if (!target.isEntity())
            return false;

        var f1 = tree.caster().getFaction();
        var f2 = target.getEntity().getFaction();
        return f2 != null && f2.equalsIgnoreCase(f1);
    }
}