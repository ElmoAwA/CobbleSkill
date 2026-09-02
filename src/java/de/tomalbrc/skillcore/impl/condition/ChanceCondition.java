package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;

public class ChanceCondition extends AbstractCondition {
    @SerializedName(value = "chance", alternate = "c")
    Resolvable<Double> chance = Resolvable.literal(0.5);

    public boolean test(SkillTree tree, Target target) {
        return Math.random() <= chance.resolve(tree, target);
    }
}