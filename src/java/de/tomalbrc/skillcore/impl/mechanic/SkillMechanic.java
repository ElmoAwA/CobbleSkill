package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.meta.MetaSkill;
import de.tomalbrc.skillcore.registry.MetaSkillRegistry;
import net.minecraft.resources.ResourceLocation;

public class SkillMechanic extends AbstractMechanic {
    @SerializedName(value = "skill", alternate = {"s"})
    protected String skill;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        MetaSkill metaSkill = MetaSkillRegistry.get(skill);

        if (metaSkill == null)
            return ExecutionResult.NULL;

        return metaSkill.cast(tree);
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SKILL;
    }
}
