package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;

public class DistanceFromTrackedLocationCondition extends AbstractCondition {
    @SerializedName(value = "distance", alternate = "d")
    RangedValue distance;

    public boolean test(SkillTree tree, Target target) {
        return distance.isWithin(tree.caster().trackingPosition().distanceTo(tree.caster().position()));
    }
}