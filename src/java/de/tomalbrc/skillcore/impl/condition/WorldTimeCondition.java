package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;

public class WorldTimeCondition extends AbstractCondition {
    @SerializedName(value = "time", alternate = "t")
    RangedValue time = RangedValue.of(0.);

    public boolean test(SkillTree tree, Target target) {
        long t = tree.level().getDayTime() % 24000;
        return time.isWithin(t);
    }
}
