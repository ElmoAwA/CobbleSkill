package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ArrowVolleyMechanic extends AbstractMechanic {
    public enum VolleyType {REGULAR, RAIN}

    @SerializedName(value = "subtype", alternate = {"source", "s"})
    VolleyType volleyType = VolleyType.REGULAR;

    @SerializedName(value = "arrows", alternate = {"a"})
    Resolvable<Integer> amount = Resolvable.literal(10);

    @SerializedName(value = "radius", alternate = {"r"})
    Resolvable<Double> radius = Resolvable.literal(0.0);

    @SerializedName(value = "yoffset", alternate = {"y"})
    Resolvable<Double> yOffset = Resolvable.literal(1.0);

    @SerializedName(value = "fireticks", alternate = {"ft", "f"})
    Resolvable<Integer> fireTicks = Resolvable.literal(0);

    @SerializedName(value = "removedelay", alternate = {"rd"})
    Resolvable<Integer> removeDelay = Resolvable.literal(200);

    @SerializedName(value = "pickup", alternate = {"canpickup"})
    Resolvable<Boolean> canPickup = Resolvable.literal(true);

    @SerializedName(value = "velocity", alternate = {"v"})
    Resolvable<Double> velocity = Resolvable.literal(1.0);

    @SerializedName(value = "poweraffectsvelocity", alternate = {"pav"})
    boolean powerAffectsVelocity = true;

    @SerializedName(value = "forwardoffset", alternate = {"startfoffset", "sfo"})
    Resolvable<Double> forwardOffset = Resolvable.literal(1.0);

    @SerializedName(value = "startsideoffset", alternate = {"ssoffset", "sso"})
    Resolvable<Double> startSideOffset = Resolvable.literal(0.0);

    @SerializedName(value = "startyoffset", alternate = {"syo"})
    Resolvable<Double> startYOff = Resolvable.literal(0.0);
    transient final Random random = new Random();

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<de.tomalbrc.skillcore.api.target.Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        int count = Math.max(0, Math.abs(amount.resolve(tree)));
        double radiusVal = Math.max(0.0, radius.resolve(tree));
        double yOff = Math.max(0.0, yOffset.resolve(tree));
        boolean allowPickup = canPickup.resolve(tree);

        double vel = this.velocity.resolve(tree);
        if (this.powerAffectsVelocity) vel *= tree.caster().getPower();

        ServerLevel level = tree.level();

        for (Target t : targets) {
            Vec3 targetPos = t.getPosition();
            Vec3 origin = tree.caster().position();

            double sy = this.startYOff.resolve(tree);
            origin = origin.add(0.0, sy, 0.0);

            double sfo = this.forwardOffset.resolve(tree);
            double sso = this.startSideOffset.resolve(tree);
            origin = moveOrigin(origin, sfo, 0.0, sso, tree);

            for (int i = 0; i < count; i++) {
                Vec3 spawnPos;
                if (volleyType == VolleyType.RAIN) {
                    spawnPos = targetPos.add(-radiusVal + random.nextDouble() * radiusVal * 2, yOff, -radiusVal + random.nextDouble() * radiusVal * 2);
                } else {
                    spawnPos = origin;
                }

                Vec3 direction = targetPos.subtract(origin).normalize().scale(vel/10.0);

                Entity proj = createProjectile("minecraft:arrow", tree, spawnPos);
                if (proj == null) continue;

                if (proj instanceof Projectile p) {
                    p.setOwner(tree.caster());
                    if (proj instanceof AbstractArrow arrow) {
                        arrow.pickup = allowPickup ? AbstractArrow.Pickup.ALLOWED : AbstractArrow.Pickup.DISALLOWED;
                        arrow.setRemainingFireTicks(this.fireTicks.resolve(tree));
                    }
                }

                proj.setDeltaMovement(direction);

                final Entity finalProj = proj;
                SkillCore.SERVER.execute(() -> level.addFreshEntity(finalProj));

                int removeDelayTicks = 0;
                removeDelayTicks = this.removeDelay.resolve(tree);
                if (removeDelayTicks > 0) {
                    // TODO: use ticking gadget

                    long delayMs = removeDelayTicks * 50L;
                    CompletableFuture.runAsync(() -> {
                        SkillCore.SERVER.execute(finalProj::discard);
                    }, CompletableFuture.delayedExecutor(delayMs, TimeUnit.MILLISECONDS, SkillCore.SERVER));
                }
            }
        }

        return ExecutionResult.NULL;
    }

    private Entity createProjectile(String name, SkillTree tree, Vec3 pos) {
        ResourceLocation rl = ResourceLocation.parse(name);

        EntityType<?> type = EntityType.byString(rl.toString()).orElse(null);
        if (type == null) return null;

        Entity e = type.create(tree.level(), null, BlockPos.containing(pos), MobSpawnType.TRIGGERED, false, false);
        if (e == null) return null;

        e.moveTo(pos);
        return e;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.ARROWVOLLEY;
    }

    public static Vec3 moveOrigin(Vec3 origin, double forward, double up, double side, SkillTree tree) {
        Vec3 look = tree.caster().getLookAngle();

        Vec3 forwardDir = new Vec3(look.x, 0, look.z);
        if (forwardDir.lengthSqr() > 0) forwardDir = forwardDir.normalize();

        Vec3 sideDir = new Vec3(-forwardDir.z, 0, forwardDir.x);
        Vec3 moved = origin;

        if (forward != 0.0) {
            moved = moved.add(forwardDir.scale(forward));
        }

        if (side != 0.0) {
            moved = moved.add(sideDir.scale(side));
        }

        if (up != 0.0) {
            moved = moved.add(0, up, 0);
        }

        return moved;
    }
}
