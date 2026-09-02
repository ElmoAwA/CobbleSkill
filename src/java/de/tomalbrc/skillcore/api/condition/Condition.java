package de.tomalbrc.skillcore.api.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

public interface Condition {

    boolean test(SkillTree tree, Target target);

    boolean testWithTrigger(SkillTree tree, Target target);

    boolean testTrigger(); // for inline conditions

    enum Action {
        @SerializedName("true")
        TRUE,
        @SerializedName("false")
        FALSE,
        @SerializedName("power")
        POWER,
        @SerializedName("cast")
        CAST,
        @SerializedName("castinstead")
        CASTINSTEAD,
        @SerializedName(value = "or_else_cast", alternate = {"orElseCast", "orelsecast"})
        OR_ELSE_CAST
    }
}
