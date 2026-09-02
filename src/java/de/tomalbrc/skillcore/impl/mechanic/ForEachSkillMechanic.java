package de.tomalbrc.skillcore.impl.mechanic;

import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.meta.MetaSkill;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.registry.MetaSkillRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ForEachSkillMechanic extends AbstractMechanic {
    String skill;

    transient private MetaSkill metaSkill;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (metaSkill == null)
            metaSkill = MetaSkillRegistry.get(skill);

        if (metaSkill != null) {
            for (Target target : tree.getCurrentTargets()) {
                // TODO: does caster change?
                metaSkill.cast(tree.copyWithTargets(List.of(target)));
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.FOR_EACH;
    }
}
