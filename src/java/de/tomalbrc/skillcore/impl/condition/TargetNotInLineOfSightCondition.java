package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class TargetNotInLineOfSightCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        LivingEntity t = ((Mob) tree.caster()).getTarget();
        if (t == null) return true;
        return !t.hasLineOfSight(tree.caster());
    }
}
