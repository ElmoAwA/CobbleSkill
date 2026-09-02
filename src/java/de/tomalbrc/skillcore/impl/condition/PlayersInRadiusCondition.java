package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;
import net.minecraft.world.entity.player.Player;

public class PlayersInRadiusCondition extends AbstractCondition {
    @SerializedName(value = "amount", alternate = "a")
    RangedValue amount = RangedValue.parse(">0");
    @SerializedName(value = "radius", alternate = {"r", "distance", "d"})
    float radius = 32;
    @SerializedName(value = "ignorespectator", alternate = "is")
    boolean ignorespectator = true;

    public boolean test(SkillTree tree, Target target) {
        return amount.isWithin(tree.getNearbyEntities(target.getPosition(), radius).stream().filter(x -> x instanceof Player p && (!ignorespectator || !p.isSpectator())).count());
    }
}
