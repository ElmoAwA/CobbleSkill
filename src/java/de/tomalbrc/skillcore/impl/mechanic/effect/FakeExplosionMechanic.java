package de.tomalbrc.skillcore.impl.mechanic.effect;

import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class FakeExplosionMechanic extends AbstractMechanic {
    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        ServerLevel level = tree.level();
        for (Target t : targets) {
            Entity target = t.getEntity();
            if (target == null) continue;

            double x = target.getX();
            double y = target.getY();
            double z = target.getZ();

            level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 12, 0.5, 0.5, 0.5, 0.05);
            level.sendParticles(ParticleTypes.SMOKE, x, y + 0.5, z, 8, 0.6, 0.2, 0.6, 0.02);

            level.playSound(
                null,
                x, y, z,
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.BLOCKS,
                4.0F,
                1.0F
            );
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.FAKEEXPLOSION;
    }
}
