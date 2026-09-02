package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

public class VariableContainsCondition extends AbstractCondition {
    private final String var;
    private final String value;

    VariableContainsCondition(String var, String value) {
        this.var = var;
        this.value = value;
    }

    public boolean test(SkillTree tree, Target target) {
        var v = tree.vars().get(var);
        return v != null && v.getRaw().toString().contains(value);
    }
}
