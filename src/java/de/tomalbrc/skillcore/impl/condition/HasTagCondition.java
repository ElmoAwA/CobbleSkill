package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;

public class HasTagCondition extends AbstractCondition {
    @SerializedName(value = "tag", alternate = "t")
    Resolvable<String> tag;

    public boolean test(SkillTree tree, Target target) {
        return target.getEntity().getTags().contains(tag.resolve(tree, target));
    }
}
