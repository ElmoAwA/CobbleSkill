package de.tomalbrc.skillcore.impl.mechanic;

import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class GotoMechanic extends AbstractMechanic {
    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (!(tree.caster() instanceof Mob mob))
            return ExecutionResult.NULL;

        for (Target target : tree.getCurrentTargets()) {
            mob.getNavigation().stop();
            mob.getNavigation().moveTo(target.getPosition().x, target.getPosition().y, target.getPosition().z, 1f);
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.GOTO;
    }
}