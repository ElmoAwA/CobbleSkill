package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;
import net.minecraft.world.entity.player.Player;

public class EnchantingExperienceCondition extends AbstractCondition {
    @SerializedName(value = "level", alternate = {"l", "amount", "a"})
    RangedValue level;

    public boolean test(SkillTree tree, Target target) {
        if (!(target.getEntity() instanceof Player p)) return false;

        int xp = p.totalExperience;
        return level.isWithin(xp);
    }
}