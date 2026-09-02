package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;
import net.minecraft.world.entity.LivingEntity;

public class HealthCondition extends AbstractCondition {
    @SerializedName(value = "health", alternate = {"h", "amount", "a"})
    RangedValue health;

    @SerializedName(value = "includeabsorption", alternate = {"ia"})
    boolean includeAbsorption = false;

    @Override
    public boolean test(SkillTree tree, Target target) {
        if (!(target.getEntity() instanceof LivingEntity living)) {
            return false;
        }

        double current = living.getHealth();

        if (includeAbsorption) {
            current += living.getAbsorptionAmount();
        }

        return health != null && health.isWithin(current);
    }
}
