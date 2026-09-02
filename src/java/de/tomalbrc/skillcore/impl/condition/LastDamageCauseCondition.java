package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

public class LastDamageCauseCondition extends AbstractCondition {
    @SerializedName(value = "damagecause", alternate = {"cause", "c"})
    String cause;

    public boolean test(SkillTree tree, Target target) {
        String o = target.getEntity().asLivingEntity().getLastDamageSource().typeHolder().getRegisteredName();
        return o.equalsIgnoreCase(cause);
    }
}