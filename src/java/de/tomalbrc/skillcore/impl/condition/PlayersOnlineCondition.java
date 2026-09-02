package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;

import java.util.Objects;

public class PlayersOnlineCondition extends AbstractCondition {
    @SerializedName(value = "amount", alternate = "a")
    RangedValue amount = RangedValue.of(0);

    public boolean test(SkillTree tree, Target target) {
        int c = Objects.requireNonNull(tree.level().getServer()).getPlayerList().getPlayers().size();
        return amount.isWithin(c);
    }
}
