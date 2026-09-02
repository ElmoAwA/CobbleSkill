package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

public class WorldCondition extends AbstractCondition {
    @SerializedName(value = "world", alternate = {"w"})
    String worldName;

    public boolean test(SkillTree tree, Target target) {
        return worldName.toLowerCase().endsWith(target.level().dimension().location().getPath());
    }
}
