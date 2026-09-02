package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;

public class MotionYCondition extends AbstractCondition {
    @SerializedName(value = "velocity", alternate = {"v"})
    RangedValue velocity;

    public boolean test(SkillTree tree, Target target) {
        if (!target.isEntity())
            return false;

        double v = target.getEntity().getDeltaMovement().y;
        return velocity.isWithin(v);
    }
}
