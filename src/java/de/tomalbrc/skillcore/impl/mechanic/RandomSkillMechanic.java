package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.registry.MetaSkillRegistry;
import de.tomalbrc.skillcore.util.WeightedSkillList;
import net.minecraft.resources.ResourceLocation;

public class RandomSkillMechanic extends AbstractMechanic {
    @SerializedName(value = "skills", alternate = {"s", "m", "meta", "metas"})
    WeightedSkillList skills;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (skills == null || skills.isEmpty()) return ExecutionResult.NULL;
        String selected = skills.pickName();
        var metaSkill = MetaSkillRegistry.get(selected);
        if (metaSkill != null) return metaSkill.cast(tree);
        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.RANDOM_SKILL;
    }
}
