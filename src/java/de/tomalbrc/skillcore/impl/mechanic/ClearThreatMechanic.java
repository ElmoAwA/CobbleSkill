package de.tomalbrc.skillcore.impl.mechanic;

import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.resources.ResourceLocation;

public class ClearThreatMechanic extends AbstractMechanic {
    @Override
    public ExecutionResult execute(SkillTree tree) {
        for (Target target : tree.getCurrentTargets()) {
            if (target.isEntity() && target.getEntity().asLivingEntity() != null && target.getEntity().asLivingEntity().isThreatTableEnabled()) {
                var table = target.getEntity().asLivingEntity().getThreatTable();
                table.clear();
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.CLEAR_THREAT;
    }
}