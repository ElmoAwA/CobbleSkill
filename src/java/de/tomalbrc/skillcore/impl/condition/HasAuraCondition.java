package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import de.tomalbrc.skillcore.impl.variable.Resolvable;

public class HasAuraCondition extends AbstractCondition {
    @SerializedName(value = "auraname", alternate = {"aura", "b", "buff", "buffname", "debuff", "debuffname", "n", "name"})
    Resolvable<String> auraName;

    public boolean test(SkillTree tree, Target target) {
        return target.isEntity() && SkillEngine.getInstance().auraManager().has(target.getEntity(), auraName.resolve(tree, target));
    }
}