package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Variable;
import de.tomalbrc.skillcore.util.RangedValue;

public class DamageAmountCondition extends AbstractCondition {
    @SerializedName(value = "damageamount", alternate = {"amount", "a"})
    RangedValue damageAmount = RangedValue.parse(">0");

    public boolean test(SkillTree tree, Target target) {
        var n = tree.vars().getOrDefault("lastDamage", Variable.EMPTY).asDoublePrimitive();
        return damageAmount.isWithin(n);
    }
}