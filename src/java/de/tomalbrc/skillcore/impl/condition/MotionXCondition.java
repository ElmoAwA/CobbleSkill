package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;

public class MotionXCondition extends AbstractCondition {
    @SerializedName(value = "velocity", alternate = {"v"})
    RangedValue velocity;

    public boolean test(SkillTree tree, Target target) {
        if (!target.isEntity())
            return false;

        double v = target.getEntity().getDeltaMovement().x;
        return velocity.isWithin(v);
    }
}
