package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;
import net.minecraft.world.entity.player.Player;

public class FoodLevelCondition extends AbstractCondition {
    @SerializedName(value = "amount", alternate = {"a", "food", "f"})
    RangedValue amount = RangedValue.of(0);

    public boolean test(SkillTree tree, Target target) {
        if (!(target.getEntity() instanceof Player p)) return false;
        return amount.isWithin(p.getFoodData().getFoodLevel());
    }
}
