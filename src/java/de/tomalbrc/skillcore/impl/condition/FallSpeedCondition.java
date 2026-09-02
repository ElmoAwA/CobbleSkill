package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;

public class FallSpeedCondition extends AbstractCondition {
    @SerializedName(value = "speed", alternate = "s")
    RangedValue fallspeed;

    public boolean test(SkillTree tree, Target target) {
        double v = Math.abs(target.getEntity().getDeltaMovement().y);
        return fallspeed.isWithin(v);
    }
}