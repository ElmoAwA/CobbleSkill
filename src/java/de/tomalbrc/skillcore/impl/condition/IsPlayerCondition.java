package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.entity.player.Player;

public class IsPlayerCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        return target.getEntity() instanceof Player;
    }
}
