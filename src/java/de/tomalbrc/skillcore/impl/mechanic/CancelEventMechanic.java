package de.tomalbrc.skillcore.impl.mechanic;

import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import net.minecraft.resources.ResourceLocation;

public class CancelEventMechanic extends AbstractMechanic {
    @Override
    public ExecutionResult execute(SkillTree tree) {
        return ExecutionResult.FAIL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.CANCEL_EVENT;
    }
}