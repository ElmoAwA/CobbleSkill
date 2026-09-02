package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

public class HeightAboveCondition extends AbstractCondition {
    @SerializedName(value = "height", alternate = "h")
    double height;

    public boolean test(SkillTree tree, Target target) {
        return target.getPosition().y() > height;
    }
}
