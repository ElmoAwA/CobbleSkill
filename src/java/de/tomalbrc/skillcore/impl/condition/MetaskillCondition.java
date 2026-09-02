package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Variable;

public class MetaskillCondition extends AbstractCondition {
    String meta;

    public boolean test(SkillTree tree, Target target) {
        // TODO: var not set!
        // TODO: store metaskill execution result in tree?
        return tree.vars().getOrDefault("metaLastResult:" + meta, Variable.EMPTY).asBoolean() == Boolean.TRUE;
    }
}