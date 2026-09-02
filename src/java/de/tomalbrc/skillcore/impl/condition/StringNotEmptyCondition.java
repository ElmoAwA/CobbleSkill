package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

public class StringNotEmptyCondition extends AbstractCondition {
    private final String var;

    StringNotEmptyCondition(String var) {
        this.var = var;
    }

    public boolean test(SkillTree tree, Target target) {
        Object v = tree.vars().get(var);
        return v != null && !v.toString().isEmpty();
    }
}
