package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;

public class StanceCondition extends AbstractCondition {
    @SerializedName(value = "stance", alternate = {"s"})
    Resolvable<String> stance = Resolvable.literal("default");

    @SerializedName(value = "strict", alternate = {"str"})
    boolean strict = true;

    public boolean test(SkillTree tree, Target target) {
        if (target.getEntity() == null)
            return false;

        var s = stance.resolve(tree, target);
        var stance = target.getEntity().getStance();
        if (strict)
            return stance.equals(s);
        else
            return stance.equalsIgnoreCase(s);
    }
}
