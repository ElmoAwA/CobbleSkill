package de.tomalbrc.skillcore.impl.target;

import com.google.common.collect.ImmutableList;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class TargetTargeter extends AbstractTargeter {

    @Override
    public List<Target> find(SkillTree tree) {
        if (tree.caster() instanceof Mob mob) {
            LivingEntity t = mob.getTarget();
            if (t == null && mob.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
                t = mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
            }
            return t == null ? ImmutableList.of() : List.of(Target.of(t));
        } else if (tree.caster() instanceof ServerPlayer player) {
            var t = getTargetedEntity(tree, player);
            if (t != null)
                return List.of(Target.of(t));
        }

        return List.of();
    }

    public static LivingEntity getTargetedEntity(SkillTree tree, Player player) {
        double range = 32.0;
        Vec3 eyePos = player.getEyePosition();
        Vec3 viewVec = player.getViewVector(1.0F);
        Vec3 maxEndVec = eyePos.add(viewVec.scale(range));

        List<Entity> nearby = tree.getNearbyEntities(player.position(), range);

        LivingEntity closestTarget = null;
        double closestDistSq = Double.MAX_VALUE;

        for (Entity en : nearby) {
            if (!(en instanceof LivingEntity living) || en == player) continue;
            if (living instanceof Player p && p.isCreative()) continue;

            if (!player.hasLineOfSight(living)) {
                continue;
            }

            AABB box = living.getBoundingBox().inflate(0.5);
            Optional<Vec3> hit = box.clip(eyePos, maxEndVec);

            if (hit.isPresent()) {
                double distSq = eyePos.distanceToSqr(hit.get());
                if (distSq < closestDistSq) {
                    closestDistSq = distSq;
                    closestTarget = living;
                }
            }
        }

        return closestTarget;
    }
}