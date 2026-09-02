package de.tomalbrc.skillcore.impl.mechanic;

import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class SetTargetMechanic extends AbstractMechanic {
    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null && tree.caster() instanceof Mob mob) {
            for (Target target : targets) {
                var e = target.getEntity();
                if (e != null && e.asLivingEntity() != null) {
                    mob.setTarget(e.asLivingEntity());
                    return ExecutionResult.NULL;
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SET_TARGET;
    }
}