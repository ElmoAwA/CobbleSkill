package de.tomalbrc.skillcore.impl.mechanic;

import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import net.minecraft.resources.ResourceLocation;

public class DelayMechanic extends AbstractMechanic {
    protected int ticks;
    transient ExecutionResult result;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (result == null)
            result = ExecutionResult.delayed(ticks);
        return result;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.DELAY;
    }
}