package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;

public class ZDiffCondition extends AbstractCondition {
    @SerializedName(value = "difference", alternate = {"diff", "d"})
    RangedValue diff;

    public boolean test(SkillTree tree, Target target) {
        double d = Math.abs(target.getPosition().z() - tree.caster().getZ());
        return diff.isWithin(d);
    }
}
