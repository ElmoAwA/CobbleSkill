package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import de.tomalbrc.skillcore.impl.variable.Variable;

import java.util.Objects;

public class VariableEqualsCondition extends AbstractCondition {
    String var;
    Object value;
    Variable.Scope scope;

    public boolean test(SkillTree tree, Target target) {
        var r = Resolvable.reference(scope, var);
        return Objects.equals(r.resolve(tree, target), value);
    }
}
