package de.tomalbrc.skillcore.impl.mechanic.aura;

import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import de.tomalbrc.skillcore.impl.aura.SimpleAura;
import net.minecraft.resources.ResourceLocation;

public class AuraMechanic extends AbstractAuraMechanic {
    @Override
    public ExecutionResult execute(SkillTree tree) {
        for (Target target : tree.getCurrentTargets()) {
            SimpleAura aura = new SimpleAura(tree, this, target);
            SkillEngine.getInstance().addAura(aura);
        }
        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.AURA;
    }
}
