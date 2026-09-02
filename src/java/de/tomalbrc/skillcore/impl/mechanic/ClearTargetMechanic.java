package de.tomalbrc.skillcore.impl.mechanic;

import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Optional;

public class ClearTargetMechanic extends AbstractMechanic {
    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (tree.getCurrentTargets() != null) {
            for (Target target : tree.getCurrentTargets()) {
                if (target.getEntity() instanceof Mob mob) {
                    SkillCore.SERVER.execute(() -> {
                        mob.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, Optional.empty());
                        mob.setTarget(null);
                    });
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.CLEAR_TARGET;
    }
}
