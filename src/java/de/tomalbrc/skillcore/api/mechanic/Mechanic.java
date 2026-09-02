package de.tomalbrc.skillcore.api.mechanic;

import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import net.minecraft.resources.ResourceLocation;

public interface Mechanic {
    ExecutionResult execute(SkillTree tree);

    ResourceLocation id();

    boolean sync();

    int delay();

    int repeat();

    int repeatInterval();

    int targetInterval();
}
