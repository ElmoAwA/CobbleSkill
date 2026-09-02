package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;

public class VariableInRangeCondition extends AbstractCondition {
    @SerializedName(value = "variable", alternate = {"name", "n", "var", "key", "k"})
    String var;
    @SerializedName(value = "value", alternate = {"val", "v", "range", "r"})
    RangedValue range;

    // TODO: scope
    public boolean test(SkillTree tree, Target target) {
        Number v = tree.vars().get(var).asDoublePrimitive();
        double val = v.doubleValue();
        return range.isWithin(val);
    }
}
