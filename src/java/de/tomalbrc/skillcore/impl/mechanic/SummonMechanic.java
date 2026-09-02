package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import de.tomalbrc.skillcore.registry.MetaSkillRegistry;
import de.tomalbrc.skillcore.registry.MobRegistry;
import de.tomalbrc.skillcore.util.RangedValue;
import de.tomalbrc.skillcore.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

public class SummonMechanic extends AbstractMechanic {

    @SerializedName(value = "subtype", alternate = {"t", "mob", "m"})
    Resolvable<String> type = Resolvable.literal("SKELETON");

    @SerializedName(value = "onsummon", alternate = {"onsummonskill", "then"})
    Resolvable<String> onSummon = Resolvable.nullable();

    @SerializedName(value = "amount", alternate = {"a"})
    Resolvable<RangedValue> amount = Resolvable.literal(RangedValue.of(1.0));

    @SerializedName(value = "level", alternate = {"l"})
    Resolvable<Integer> level = Resolvable.literal(0);

    @SerializedName(value = "yaw")
    Resolvable<Double> yaw = Resolvable.nullable();

    @SerializedName(value = "pitch")
    Resolvable<Double> pitch = Resolvable.nullable();

    @SerializedName(value = "usetargetyaw", alternate = {"uty"})
    Resolvable<Boolean> useTargetYaw = Resolvable.literal(false);

    @SerializedName(value = "usetargetpitch", alternate = {"utp"})
    Resolvable<Boolean> useTargetPitch = Resolvable.literal(false);

    @SerializedName(value = "radius", alternate = {"r", "noise", "n"})
    Resolvable<Double> radius = Resolvable.literal(0.0);

    @SerializedName(value = "yradius", alternate = {"yr", "ynoise", "yn"})
    Resolvable<Double> yRadius = Resolvable.nullable();

    @SerializedName(value = "yradiusuponly", alternate = {"yradiusonlyup", "yruo", "yu"})
    Resolvable<Boolean> yRadiusUpOnly = Resolvable.literal(false);

    @SerializedName(value = "velocity", alternate = {"v", "force", "f"})
    Resolvable<Double> velocity = Resolvable.literal(0.0);

    @SerializedName(value = "yvelocity", alternate = {"yv", "yforce", "yf"})
    Resolvable<Double> yvelocity = Resolvable.nullable();

    @SerializedName(value = "onsurface", alternate = {"os", "s"})
    Resolvable<Boolean> onSurface = Resolvable.literal(false);

    @SerializedName(value = "copythreattable", alternate = {"ctt"})
    Resolvable<Boolean> copyThreatTable = Resolvable.literal(false);

    @SerializedName(value = "inheritthreattable", alternate = {"itt"})
    Resolvable<Boolean> inheritThreatTable = Resolvable.literal(false);

    @SerializedName(value = "inheritfaction", alternate = {"if"})
    Resolvable<Boolean> inheritFaction = Resolvable.literal(true);

    @SerializedName(value = "inheritdespawn", alternate = {"inheritdespawnoption", "ido"})
    Resolvable<Boolean> inheritDespawn = Resolvable.literal(false);

    @SerializedName(value = "summonerisowner", alternate = {"sio"})
    Resolvable<Boolean> summonerIsOwner = Resolvable.literal(true);

    @SerializedName(value = "summonerisparent", alternate = {"sip"})
    Resolvable<Boolean> summonerIsParent = Resolvable.literal(true);

    transient final Random random = new Random();

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        String typeName = type.resolve(tree);
        int amt = Math.abs(amount.resolve(tree).getAsInteger());
        double r = radius.resolve(tree);
        Double yr = yRadius.resolve(tree);
        double vy = velocity.resolve(tree);
        Double yv = yvelocity.resolve(tree);
        if (yr == null) yr = r;
        if (yv == null) yv = vy;

        boolean placeOnSurface = onSurface.resolve(tree);

        for (Target t : targets) {
            int lvl = Math.max(0, level.resolve(tree, t));

            Vec3 base = t.getPosition();

            for (int i = 0; i < amt; i++) {
                double dx = (random.nextDouble() * 2.0 - 1.0) * r;
                double dz = (random.nextDouble() * 2.0 - 1.0) * r;
                double dy;
                if (yRadiusUpOnly.resolve(tree)) {
                    dy = random.nextDouble() * yr;
                } else {
                    dy = (random.nextDouble() * 2.0 - 1.0) * yr;
                }

                Vec3 candidateBase = base.add(dx, dy, dz);

                Vec3 spawnPos = Util.safeSpawnPosition(tree.level(), candidateBase, r, 0, 1, false, placeOnSurface);
                if (spawnPos == null) {
                    --i;
                    continue;
                }

                Double setYaw = yaw.resolve(tree);
                Double setPitch = pitch.resolve(tree);

                if (setYaw == null && useTargetYaw.resolve(tree)) {
                    setYaw = (double) t.getYRot();
                }

                if (setPitch == null && useTargetPitch.resolve(tree)) {
                    setPitch = (double) t.getXRot();
                }

                if (setYaw == null && tree.caster() != null) {
                    setYaw = (double) tree.caster().getYRot();
                }

                if (setPitch == null && tree.caster() != null) {
                    setPitch = (double) tree.caster().getXRot();
                }

                Double finalSetYaw = setYaw;
                Double finalSetPitch = setPitch;
                Double finalYv = yv;
                SkillCore.SERVER.execute(() -> {
                    Entity spawned = createEntity(typeName, tree, spawnPos);
                    spawned.setCustomLevel(lvl);

                    if (finalSetYaw != null) spawned.setYRot((float) (finalSetYaw % 360.0));
                    if (finalSetPitch != null) spawned.setXRot((float) (finalSetPitch % 360.0));

                    double vx = (random.nextDouble() * 2 - 1) * vy;
                    double vz = (random.nextDouble() * 2 - 1) * vy;
                    double vyy = (random.nextDouble() * 2 - 1) * finalYv;
                    spawned.setDeltaMovement(vx, vyy, vz);

                    if (spawned instanceof Wolf le) {
                        if (summonerIsOwner.resolve(tree) && tree.caster() instanceof LivingEntity owner) {
                            le.setOwnerUUID(owner.getUUID());
                        }
                        // TODO: summonerIsParent behavior
                    }

                    tree.level().addFreshEntity(spawned);

                    if (onSummon != null && onSummon.resolve(tree) != null) {
                        MetaSkillRegistry.getOptional(onSummon.resolve(tree)).ifPresent(s ->
                                s.cast(tree.copyWithTargets(List.of(Target.of(spawned))).copyCaster(t.getEntity()))
                        );
                    }
                });


            }
        }

        return ExecutionResult.NULL;
    }

    private Entity createEntity(String typeName, SkillTree tree, Vec3 pos) {
        ServerLevel level = tree.level();

        EntityType<?> et = EntityType.byString(typeName.toLowerCase()).orElse(null);
        Entity e = null;

        if (et != null) {
            e = et.create(level, null, BlockPos.containing(pos), MobSpawnType.TRIGGERED, false, false);
        } else {
            var data = MobRegistry.get(typeName);
            if (data != null) e = data.spawn(level, pos, false);
        }

        if (e != null) e.moveTo(pos);

        return e;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SUMMON;
    }
}
