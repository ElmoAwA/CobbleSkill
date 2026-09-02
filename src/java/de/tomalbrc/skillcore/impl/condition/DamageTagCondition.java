package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Variable;
import net.minecraft.resources.ResourceLocation;

public class DamageTagCondition extends AbstractCondition {
    ResourceLocation tag;
    // TODO: bukkit compat
    public boolean test(SkillTree tree, Target target) {
        return tree.vars().getOrDefault("damageTags", Variable.EMPTY).asSet().contains(tag);
    }
}
