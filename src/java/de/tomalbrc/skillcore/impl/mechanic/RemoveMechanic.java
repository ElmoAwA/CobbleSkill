package de.tomalbrc.skillcore.impl.mechanic;

import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.resources.ResourceLocation;

public class RemoveMechanic extends AbstractMechanic {
    @Override
    public ExecutionResult execute(SkillTree tree) {
        for (Target target : tree.getCurrentTargets()) {
            SkillCore.SERVER.execute(() -> {
                if (target.getEntity() != null)
                    target.getEntity().discard();
            });
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.REMOVE;
    }
}