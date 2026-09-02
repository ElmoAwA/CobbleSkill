package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;

public class SkillOnCooldownCondition extends AbstractCondition {
    @SerializedName(value = "skill", alternate = "s")
    Resolvable<String> skill;

    public boolean test(SkillTree tree, Target target) {
        return target.isEntity() && target.getEntity().overlay() != null && target.getEntity().overlay().isOnCooldown(skill.resolve(tree, target));
    }
}
