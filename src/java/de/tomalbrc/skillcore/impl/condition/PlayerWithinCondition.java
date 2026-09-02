package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.world.entity.player.Player;

public class PlayerWithinCondition extends AbstractCondition {
    @SerializedName(value = "radius", alternate = {"r", "d", "distance"})
    Resolvable<Double> radius = Resolvable.literal(0.);

    public boolean test(SkillTree tree, Target target) {
        return tree.getNearbyEntities(target.getPosition(), radius.resolve(tree, target)).stream().anyMatch(x -> x instanceof Player);
    }
}
