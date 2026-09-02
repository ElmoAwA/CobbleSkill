package de.tomalbrc.skillcore.impl.mechanic;

import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class MountTargetMechanic extends AbstractMechanic {

    @Override
    public ExecutionResult execute(SkillTree tree) {
        var caster = tree.caster();

        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        for (Target target : targets) {
            Entity t = target.getEntity();
            if (t != null) {
                caster.startRiding(t, true);
                return ExecutionResult.NULL;
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.MOUNTTARGET;
    }
}
