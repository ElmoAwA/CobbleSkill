package de.tomalbrc.skillcore.impl.mechanic;

import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import net.minecraft.resources.ResourceLocation;

public class CancelSkillMechanic extends AbstractMechanic {
    @Override
    public ExecutionResult execute(SkillTree tree) {
        tree.cancel();
        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.CANCEL_SKILL;
    }

}