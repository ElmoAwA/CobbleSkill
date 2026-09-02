package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.entity.LivingEntity;

public class BlockingCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        if (!(target.getEntity() instanceof LivingEntity p)) return false;
        return p.isBlocking();
    }
}