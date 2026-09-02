package de.tomalbrc.skillcore.impl.mechanic;

import com.google.common.base.Predicates;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.mixin.accessor.MobAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class ResetAiMechanic extends AbstractMechanic {
    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                if (target.getEntity() instanceof Mob mob) {
                    SkillCore.SERVER.execute(() -> {
                        ((MobAccessor)mob).getTargetSelector().removeAllGoals(Predicates.alwaysTrue());
                        ((MobAccessor)mob).getGoalSelector().removeAllGoals(Predicates.alwaysTrue());
                        ((MobAccessor)mob).invokeRegisterGoals();
                    });
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.RESETAI;
    }
}