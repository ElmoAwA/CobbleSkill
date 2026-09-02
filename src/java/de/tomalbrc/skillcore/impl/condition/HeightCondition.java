package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;

public class HeightCondition extends AbstractCondition {
    @SerializedName(value = "height", alternate = {"h"})
    RangedValue height;

    public boolean test(SkillTree tree, Target target) {
        double y = target.getPosition().y();
        return height.isWithin(y);
    }
}
