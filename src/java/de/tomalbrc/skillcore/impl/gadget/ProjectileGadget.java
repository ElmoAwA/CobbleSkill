package de.tomalbrc.skillcore.impl.gadget;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.projectile.ProjectileMechanic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ProjectileGadget extends AbstractProjectileGadget<ProjectileMechanic> {
    private Vec3 currentVel;

    private int climbAttempts = 0;
    private int dropAttempts = 0;

    public ProjectileGadget(SkillTree tree, ProjectileMechanic mechanic, Target target) {
        super(tree, mechanic, target);

        var meteor = "meteor".equalsIgnoreCase(mechanic.type);
        if (meteor) {
            double heightFromSurface = meteor ? mechanic.heightFromSurface : 0;
            currentPos = target.getPosition().add(0, heightFromSurface, 0);
        }

        Vec3 dir = currentPos.vectorTo(target.getPosition().add(0, mechanic.targetYOffset, 0));
        if (dir.lengthSqr() == 0) dir = new Vec3(0, 0, 1);
        dir = dir.normalize();

        // verticalOffset as percent grade
        double slope = mechanic.verticalOffset;
        double hx = dir.x;
        double hz = dir.z;
        double horizLen = Math.sqrt(hx * hx + hz * hz);

        if (horizLen > 0.0 && slope != 0.0) {
            double vy = horizLen * slope; // rise = run * slope
            dir = new Vec3(dir.x, vy, dir.z).normalize();
        }

        this.currentVel = dir.scale(mechanic.velocity / 20.0 / (mechanic.tickinterpolation + 1));
    }

    @Override
    public void onAsyncTick() {
        final double dt = 1.0 / 20.0;

        this.oldPos = currentPos;
        this.currentVel = this.currentVel.add(0, -mechanic.gravity * dt / (mechanic.tickinterpolation + 1), 0);
        this.currentPos = this.getPos().add(this.currentVel);

        if (mechanic.hugSurface) {
            var world = level();
            double px = this.currentPos.x;
            double pz = this.currentPos.z;

            int startY = BlockPos.containing(this.currentPos).getY();

            int maxDrop = Math.max(1, (int) (mechanic.maxDropHeight > 0 ? mechanic.maxDropHeight : 10));
            int maxClimb = Math.max(1, (int) (mechanic.maxClimbHeight > 0 ? mechanic.maxClimbHeight : 3));
            double heightFromSurface = mechanic.heightFromSurface;

            boolean foundSurface = false;
            double surfaceY = Double.NEGATIVE_INFINITY;

            for (int d = 0; d <= maxDrop; d++) {
                int checkY = startY - d;
                BlockPos checkPos = BlockPos.containing(px, checkY, pz);
                BlockState state = world.getBlockState(checkPos);
                VoxelShape shape = state.getCollisionShape(world, checkPos);
                boolean hasSolidShape = !shape.isEmpty();
                boolean isLiquid = !state.getFluidState().isEmpty();

                if (hasSolidShape || (mechanic.hugLiquid && isLiquid)) {
                    double topY;
                    if (hasSolidShape) {
                        topY = checkPos.getY() + shape.max(Direction.Axis.Y);
                    } else {
                        topY = checkPos.getY() + 1.0;
                    }
                    surfaceY = topY;
                    foundSurface = true;
                    break;
                }
            }

            if (foundSurface) {
                double desiredY = surfaceY + heightFromSurface;
                double eps = 1e-3;

                if (this.currentPos.y + eps < desiredY) {
                    if (climbAttempts < maxClimb) {
                        this.currentPos = new Vec3(this.currentPos.x, desiredY, this.currentPos.z);
                        this.currentVel = new Vec3(this.currentVel.x, 0, this.currentVel.z);
                        climbAttempts++;
                        dropAttempts = 0;
                    } else {
                        destroy();
                        return;
                    }
                } else if (this.currentPos.y - eps > desiredY) {
                    if (dropAttempts < maxDrop) {
                        this.currentPos = new Vec3(this.currentPos.x, desiredY, this.currentPos.z);
                        this.currentVel = new Vec3(this.currentVel.x, 0, this.currentVel.z);
                        dropAttempts++;
                        climbAttempts = 0;
                    } else {
                        destroy();
                        return;
                    }
                } else {
                    climbAttempts = 0;
                    dropAttempts = 0;
                    this.currentVel = new Vec3(this.currentVel.x, 0, this.currentVel.z);
                }
            } else {
                destroy();
                return;
            }
        }

        super.onAsyncTick();

        if (hits > 0) {
            destroy();
        }
    }

    @Override
    public void onHit(Entity entity) {
        if (mechanic.hitTargetOnly && entity != target.getEntity()) {
            return;
        } else if (!mechanic.hitTargetOnly) {
            boolean canHit = mechanic.hitSelf || entity != initialTree.caster();
            canHit = canHit && (mechanic.hitPlayers || !(entity instanceof Player));
            canHit = canHit && (mechanic.hitNonPlayers || (entity instanceof Player));
            canHit = canHit && (!mechanic.hitTarget || target.getEntity() == entity);

            if (canHit)
                super.onHit(entity);
        }
    }
}
