package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.condition.Condition;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

import java.util.List;

public class AnyCondition extends AbstractCondition {
    List<Condition> conditions = List.of();

    @Override
    public boolean test(SkillTree tree, Target target) {
        for (int i = 0; i < conditions.size(); i++) {
            if (conditions.get(i).testWithTrigger(tree, target))
                return true;
        }

        return false;
    }
}
