package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;
import net.minecraft.world.entity.LivingEntity;

public class HealthPercentCondition extends AbstractCondition {
    @SerializedName(value = "percent", alternate = {"p", "healthpercent", "hp"})
    RangedValue percent;

    @SerializedName(value = "includeabsorption", alternate = {"ia"})
    boolean includeAbsorption = false;

    @Override
    public boolean test(SkillTree tree, Target target) {
        if (!(target.getEntity() instanceof LivingEntity)) {
            return false;
        }

        LivingEntity living = (LivingEntity) target.getEntity();

        double currentHealth = living.getHealth();
        double maxHealth = living.getMaxHealth();  // base max health + modifiers (via generic.max_health attribute) :contentReference[oaicite:0]{index=0}

        if (includeAbsorption) {
            // absorption extra health
            currentHealth += living.getAbsorptionAmount();
        }

        if (maxHealth <= 0) {
            return false;
        }

        double normalized = currentHealth / maxHealth;
        // percent is interpreted as fraction: e.g. 0.5 = 50%, 1.0 = 100%
        return percent != null && percent.isWithin(normalized);
    }
}