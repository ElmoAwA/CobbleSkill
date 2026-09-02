package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;

public class NameCondition extends AbstractCondition {
    @SerializedName(value = "name", alternate = "n")
    Resolvable<String> name = Resolvable.literal("Pinnit");

    public boolean test(SkillTree tree, Target target) {
        if (!target.isEntity())
            return false;

        return target.getEntity().getName().getString().equals(name.resolve(tree, target));
    }
}
