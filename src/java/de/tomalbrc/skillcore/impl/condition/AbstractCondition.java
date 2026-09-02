package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.condition.Condition;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.registry.MetaSkillRegistry;

public abstract class AbstractCondition implements Condition {
    Action action = Action.TRUE;
    Double multiplier;
    String metaskill;
    @SerializedName("test_trigger") boolean testTrigger;

    @Override
    public boolean testTrigger() {
        return testTrigger;
    }

    public Action action() {
        return action;
    }

    public Double multiplier() {
        return multiplier;
    }

    public String skill() {
        return metaskill;
    }

    @Override
    public boolean testWithTrigger(SkillTree tree, Target target) {
        var result = this.test(tree, target);

        if (result) {
            if (action == null || action == Action.TRUE || action == Action.OR_ELSE_CAST)
                return true;
            else if (action == Action.POWER) {
                // TODO: implement power
                return true;
            } else if (action == Action.CAST) {
                cast(tree);
                return true;
            } else if (action == Action.CASTINSTEAD) {
                cast(tree);
                return false;
            } else
                return false;
        }
        else if (action == Action.FALSE ||action == Action.CASTINSTEAD)
            return true;
        else if (action == Action.OR_ELSE_CAST) {
            cast(tree);
            return false;
        }

        return false;
    }

    protected void cast(SkillTree tree) {
        var meta = MetaSkillRegistry.get(metaskill);
        if (meta != null) meta.castAsync(tree);
    }
}
