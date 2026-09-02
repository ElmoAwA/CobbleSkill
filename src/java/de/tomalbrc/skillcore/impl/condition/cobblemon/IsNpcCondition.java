package de.tomalbrc.skillcore.impl.condition.cobblemon;

import com.cobblemon.mod.common.entity.npc.NPCEntity;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.condition.AbstractCondition;

public class IsNpcCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        return target.getEntity() instanceof NPCEntity;
    }
}