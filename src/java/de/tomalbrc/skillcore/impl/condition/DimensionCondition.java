package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

import java.util.Locale;

public class DimensionCondition extends AbstractCondition {
    @SerializedName(value = "dimension", alternate = {"d", "environment", "env"})
    String dim;

    public boolean test(SkillTree tree, Target target){
        return tree.level().dimension().location().toString().contains(dim.toLowerCase(Locale.ROOT));
    }
}