package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;

public class DistanceCondition extends AbstractCondition {
    @SerializedName(value = "distance", alternate = {"d", "r", "radius"})
    RangedValue distance;

    public boolean test(SkillTree tree, Target target) {
        double d = tree.caster().distanceToSqr(target.getPosition());
        return distance.isWithin(Math.sqrt(d));
    }
}
