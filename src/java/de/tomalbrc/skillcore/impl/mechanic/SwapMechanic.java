package de.tomalbrc.skillcore.impl.mechanic;

import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;

public class SwapMechanic extends AbstractMechanic {
    // TODO: add dimensional option?

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        Entity caster = tree.caster();

        for (Target target : targets) {
            Entity entity = target.getEntity();
            if (entity == null) continue;

            doSwap(caster, entity);
        }

        return ExecutionResult.NULL;
    }

    private void doSwap(Entity caster, Entity target) {
        Vec3 casterPos = caster.position();
        float casterYRot = caster.getYRot();
        float casterXRot = caster.getXRot();
        ServerLevel casterLevel = (ServerLevel) caster.level();

        Vec3 targetPos = target.position();

        caster.teleportTo((ServerLevel) target.level(),
            targetPos.x, targetPos.y, targetPos.z, Set.of(),
            target.getYRot(), target.getXRot()
        );

        target.teleportTo(casterLevel,
            casterPos.x, casterPos.y, casterPos.z, Set.of(),
            casterYRot, casterXRot
        );
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SWAP;
    }
}
