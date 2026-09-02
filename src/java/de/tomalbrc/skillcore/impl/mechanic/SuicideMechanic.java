package de.tomalbrc.skillcore.impl.mechanic;

import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.resources.ResourceLocation;

public class SuicideMechanic extends AbstractMechanic {
    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (tree.getCurrentTargets() != null) {
            for (Target target : tree.getCurrentTargets()) {
                SkillCore.SERVER.execute(() -> {
                    if (target.getEntity() != null)
                        target.getEntity().kill();
                });
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SUICIDE;
    }
}