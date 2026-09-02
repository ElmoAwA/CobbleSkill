package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

public class VariableIsSetCondition extends AbstractCondition {
    private final String var;

    VariableIsSetCondition(String var) {
        this.var = var;
    }

    public boolean test(SkillTree tree, Target target) {
        return tree.vars().containsKey(var);
    }
}
