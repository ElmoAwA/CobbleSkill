package de.tomalbrc.skillcore.impl.mechanic.effect;

import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LevelEvent;

import java.util.List;

public class EnderEffectMechanic extends AbstractMechanic {
    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                tree.level().levelEvent(LevelEvent.PARTICLES_EYE_OF_ENDER_DEATH, target.getBlockPos(), 0);
            }
        }

        return ExecutionResult.NULL;
    }


    @Override
    public ResourceLocation id() {
        return Mechanics.ENDER_EFFECT;
    }
}