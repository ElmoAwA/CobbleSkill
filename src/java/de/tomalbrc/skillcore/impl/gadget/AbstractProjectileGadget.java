package de.tomalbrc.skillcore.impl.gadget;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.projectile.AbstractProjectileMechanic;
import de.tomalbrc.skillcore.impl.mechanic.projectile.Bullet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class AbstractProjectileGadget<T extends AbstractProjectileMechanic> extends AbstractTickingGadget {
    protected int hits;
    protected T mechanic;
    protected Target target;
    protected Vec3 oldPos;

    boolean finished = false;

    int delayTicks = 0;

    Bullet bullet;

    public AbstractProjectileGadget(SkillTree tree, T mechanic, Target target) {
        super(tree, mechanic.duration, mechanic.interval);
        this.target = target;
        this.mechanic = mechanic;

        this.currentPos = tree.origin().add(tree.caster().getForward().normalize().scale(mechanic.startFOffset));
        this.oldPos = this.currentPos;

        mechanic.onStartSkill.cast(() -> initialTree.copyWithOrigin(currentPos));

        if (mechanic.bulletType != null && !mechanic.bulletType.isBlank()) {
            this.bullet = Bullet.from(mechanic, tree.level(), currentPos);
        }
    }

    public void updateBullet() {
        if (bullet != null) {
            bullet.update(currentPos, oldPos);
        }
    }

    @Override
    public boolean shouldStop() {
        return !mechanic.stopconditions.stream().allMatch(x -> x.test(initialTree, target));
    }

    @Override
    public boolean finished() {
        return this.finished;
    }

    @Override
    public void destroy() {
        onEnd();
        this.finished = true;

        if (this.bullet != null)
            this.bullet.destroy();

        super.destroy();
    }

    @Override
    public void onAsyncTick() {
        mechanic.onTickSkill.cast(() -> initialTree.copyWithOrigin(this.currentPos));

        if (delayTicks == 0) {
            Vec3 start = this.oldPos;
            Vec3 end = this.currentPos;

            double horiz = mechanic.horizontalRadius.resolve(initialTree);
            double vert = mechanic.verticalRadius(initialTree, Target.of(level(), currentPos));

            AABB projectileBB = new AABB(start, end).inflate(horiz, vert, horiz);

            initialTree.copyWithOrigin(this.currentPos)
                    .getNearbyEntities(currentPos, horiz)
                    .stream()
                    .filter(e -> e != initialTree.caster())
                    .forEach(entity -> {
                        var entityBB = entity.getBoundingBox();

                        if (projectileBB.intersects(entityBB)) {
                            if (mechanic.hitConditions.stream().allMatch(c -> c.test(initialTree, Target.of(entity)))) {
                                onHit(entity);
                            }
                        }
                    });
        }


        if (delayTicks == 0) {
            var bp = BlockPos.containing(this.currentPos);
            var state = initialTree.level().getBlockState(bp);
            // TODO: check for hit between current and last point

            var res = state.getCollisionShape(initialTree.level(), bp).clip(this.currentPos, this.oldPos, bp);
            if (res != null && res.isInside()) {
                if (mechanic.hitConditions.stream().allMatch(c -> c.test(initialTree, Target.of(level(), this.currentPos))))
                    onHitBlock(bp);
            }
        }

        updateBullet();

        if (delayTicks == 1)
            destroy();
        else if (delayTicks > 1) delayTicks--;
    }

    public void onHitBlock(BlockPos bp) {
        mechanic.onHitBlockSkill.cast(() -> initialTree.copyWith(this.currentPos, List.of(Target.of(level(), currentPos))));

        if (mechanic.stopAtBlock && (mechanic.stopconditions == null || mechanic.stopconditions.stream().allMatch(c -> c.test(initialTree, Target.of(level(), new Vec3(bp.getX(), bp.getY(), bp.getZ()))))))
            delayTicks = mechanic.deathDelay + 1;
    }

    @Override
    public void onHit(Entity entity) {
        hits++;

        if (mechanic.onHitSkill != null) {
            mechanic.onHitSkill.cast(() -> initialTree.copyWith(this.currentPos, List.of(Target.of(entity))).copyWithTrigger(entity));
        }

        if (mechanic.doEndSkillOnHit) {
            mechanic.onEndSkill.cast(() -> initialTree.copyWith(this.currentPos, List.of(Target.of(entity))));
        }

        if (mechanic.stopAtEntity && (mechanic.stopconditions == null || mechanic.stopconditions.stream().allMatch(c -> c.test(initialTree, Target.of(entity)))))
            delayTicks = mechanic.deathDelay + 1;
    }

    @Override
    public void onEnd() {
        if (mechanic.onEndSkill != null) {
            mechanic.onEndSkill.cast(() -> initialTree.copyWith(this.currentPos, List.of(Target.of(level(), currentPos))));
        }
    }
}
