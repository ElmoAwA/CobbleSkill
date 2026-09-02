package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class TargetInLineOfSightCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        if (!(tree.caster() instanceof Mob mob))
            return false;

        LivingEntity t = mob.getTarget();
        if (t == null) return false;
        return t.hasLineOfSight(tree.caster());
    }
}
