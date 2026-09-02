package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.mixin.accessor.CombatTrackerAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class InCombatCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        if (target.getEntity() instanceof LivingEntity livingEntity) {
            return ((CombatTrackerAccessor)livingEntity.getCombatTracker()).getInCombat() || (livingEntity instanceof Mob mob && mob.getTarget() != null);
        }

        return false;
    }
}
