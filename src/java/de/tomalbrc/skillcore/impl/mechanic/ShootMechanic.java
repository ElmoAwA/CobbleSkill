package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import de.tomalbrc.skillcore.mixin.accessor.AbstractArrowAccessor;
import de.tomalbrc.skillcore.registry.ItemRegistry;
import de.tomalbrc.skillcore.registry.MobRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ShootMechanic extends AbstractMechanic {
    @SerializedName(value = "subtype", alternate = {"t"})
    Resolvable<String> type = Resolvable.literal("arrow");

    @SerializedName(value = "damage", alternate = {"d", "amount"})
    Resolvable<Double> damage = Resolvable.literal(5.0);

    @SerializedName(value = "velocity", alternate = {"v"})
    Resolvable<Double> velocity = Resolvable.literal(1.0);

    @SerializedName(value = "maxdistance", alternate = {"md"})
    Resolvable<Double> maxDistance = Resolvable.literal(64.0);

    @SerializedName(value = "poweraffectsvelocity", alternate = {"pav"})
    boolean powerAffectsVelocity = true;

    @SerializedName(value = "accuracy", alternate = {"ac", "a"})
    Resolvable<Double> accuracy = Resolvable.literal(1.0);

    @SerializedName(value = "knockback", alternate = {"kb"})
    Resolvable<Double> knockback = Resolvable.literal(0.0);

    @SerializedName(value = "piercelevel", alternate = {"pl"})
    Resolvable<Integer> pierceLevel = Resolvable.literal(0);

    @SerializedName(value = "verticaloffset", alternate = {"vo"})
    Resolvable<Double> verticalOffset = Resolvable.literal(0.0);

    @SerializedName(value = "horizontaloffset", alternate = {"ho"})
    Resolvable<Double> horizontalOffset = Resolvable.literal(0.0);

    @SerializedName(value = "forwardoffset", alternate = {"startfoffset", "sfo"})
    Resolvable<Double> forwardOffset = Resolvable.literal(1.0);

    @SerializedName(value = "sideoffset", alternate = {"soffset", "so"})
    Resolvable<Double> sideOffset = Resolvable.literal(0.0);

    @SerializedName(value = "startsideoffset", alternate = {"ssoffset", "sso"})
    Resolvable<Double> startSideOffset = Resolvable.literal(0.0);

    @SerializedName(value = "startyoffset", alternate = {"syo"})
    Resolvable<Double> startYOff = Resolvable.literal(0.0);

    @SerializedName(value = "gravity", alternate = {"g"})
    Resolvable<Boolean> gravity = Resolvable.literal(true);

    @SerializedName(value = "item")
    Resolvable<String> item = Resolvable.literal("");

    @SerializedName(value = "fromorigin", alternate = {"fo"})
    Resolvable<Boolean> fromOrigin = Resolvable.literal(false);

    // TODO:
    // bounce, pickup, ontick/onhit/onend skills, expiration

    private final transient Random random = new Random();

    // TODO: power, knockback
    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        Entity casterEnt = tree.caster();
        if (casterEnt == null) return ExecutionResult.NULL;
        ServerLevel level = tree.level();

        String ttype = type.resolve(tree).trim().toLowerCase(Locale.ROOT);

        double baseDamage = damage.resolve(tree);
        double vel = velocity.resolve(tree);
        double acc = Math.max(0.0, Math.min(1.0, accuracy.resolve(tree)));

        double kb = knockback.resolve(tree);
        int pierce = pierceLevel.resolve(tree);
        boolean useOrigin = fromOrigin.resolve(tree);
        boolean grav = gravity.resolve(tree);

        for (Target t : targets) {
            Entity targetEntity = t.getEntity();
            if (targetEntity == null) continue;

            Vec3 origin = useOrigin ? tree.origin() : casterEnt.position();
            double ox = origin.x();
            double oy = origin.y() + startYOff.resolve(tree); // small start y offset
            double oz = origin.z();

            float yaw = casterEnt.getYRot();
            double rad = Math.toRadians(yaw);

            double fx = -Math.sin(rad);
            double fz = Math.cos(rad);

            double rx = Math.cos(rad);
            double rz = Math.sin(rad);

            double spawnX = ox + fx * forwardOffset.resolve(tree) + rx * startSideOffset.resolve(tree);
            double spawnY = oy + startYOff.resolve(tree);
            double spawnZ = oz + fz * forwardOffset.resolve(tree) + rz * startSideOffset.resolve(tree);

            double tx = targetEntity.getX();
            double ty = targetEntity.getEyeY();
            double tz = targetEntity.getZ();

            double dx = tx - spawnX + horizontalOffset.resolve(tree);
            double dy = ty - spawnY + verticalOffset.resolve(tree);
            double dz = tz - spawnZ;

            double horizNoise = (1.0 - acc) * 0.45;
            double vertNoise = (1.0 - acc) * 0.10;

            dx += (random.nextDouble() * 2 - 1) * horizNoise;
            dz += (random.nextDouble() * 2 - 1) * horizNoise;
            dy += (random.nextDouble() * 2 - 1) * vertNoise;

            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist == 0) {
                dx = (random.nextDouble() * 2 - 1) * 0.01;
                dy = 0.01;
                dz = (random.nextDouble() * 2 - 1) * 0.01;
                dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            }
            dx /= dist;
            dy /= dist;
            dz /= dist;

            float finalSpeed = (float) vel;
            if (powerAffectsVelocity) {
                double power = 1.0;
                finalSpeed *= (1.0f + (float) power);
            }

            Entity projectile = createProjectileEntity(ttype, level, casterEnt, item.resolve(tree));
            if (projectile == null) {
                continue;
            }

            projectile.setPos(spawnX, spawnY, spawnZ);

            if (projectile instanceof AbstractArrow arrow) {
                ((AbstractArrowAccessor)arrow).setPickup(AbstractArrow.Pickup.DISALLOWED);
                ((AbstractArrowAccessor)arrow).setLife(Integer.MAX_VALUE);
                arrow.setBaseDamage(baseDamage);
                ((AbstractArrowAccessor)arrow).invokeSetPierceLevel((byte)pierce);
            }

            if (projectile instanceof Projectile projectile1)
                projectile1.setOwner(casterEnt);

            projectile.setNoGravity(!grav);
            projectile.setDeltaMovement(new Vec3(dx * finalSpeed, dy * finalSpeed, dz * finalSpeed));

            SkillCore.SERVER.execute(() -> level.addFreshEntity(projectile));
        }

        return ExecutionResult.NULL;
    }

    private Entity createProjectileEntity(String type, ServerLevel level, Entity shooter, String itemString) {
        String t = type.trim().toLowerCase(Locale.ROOT);

        switch (t) {
            case "arrow":
            case "arrows":
                return new Arrow(EntityType.ARROW, level);
            case "snowball":
                return new Snowball(level, (LivingEntity) shooter);
            case "egg":
                return new ThrownEgg(level, (LivingEntity) shooter);
            case "enderpearl":
            case "ender_pearl":
                return new ThrownEnderpearl(level, (LivingEntity) shooter);
            case "potion":
            case "splash_potion":
            case "splashpotion":
                ItemStack potionStack = parseItemStack(itemString, level);
                if (potionStack != null && potionStack.getItem() instanceof PotionItem) {
                    ThrownPotion pe = new ThrownPotion(level, (LivingEntity) shooter);
                    pe.setItem(potionStack);
                    return pe;
                }
            case "lingering_potion":
            case "lingeringpotion":
                return new ThrownPotion(level, (LivingEntity) shooter);

            case "item":
                ItemStack stack = parseItemStack(itemString, level);
                if (stack == null) stack = new ItemStack(Items.STONE);
                ItemEntity ie = new ItemEntity(level, shooter.getX(), shooter.getY(), shooter.getZ(), stack);
                return ie;

            case "falling_block":
            case "block":
            case "fallingblock":
                FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, shooter.getOnPos(), level.getBlockState(shooter.getOnPos()));
                fallingBlock.moveTo(shooter.getEyePosition());
                return fallingBlock;

            case "trident":
            case "tridententity":
                return new ThrownTrident(level, (LivingEntity) shooter, new ItemStack(Items.TRIDENT));
            default:
                ResourceLocation id = ResourceLocation.parse(type);
                if (BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
                    var et = BuiltInRegistries.ENTITY_TYPE.get(id);
                    return et.create(level, null, BlockPos.containing(shooter.getEyePosition()), MobSpawnType.TRIGGERED, false, false);
                } else {
                    return MobRegistry.get(type).spawn((ServerLevel) level, shooter.position(), false);
                }
        }
    }

    private ItemStack parseItemStack(String s, Level level) {
        if (s == null || s.isBlank()) return null;
        String in = s.trim();

        try {
            ResourceLocation rl = ResourceLocation.parse(in);
            var itemObj = BuiltInRegistries.ITEM.get(rl);
            return new ItemStack(itemObj);
        } catch (Throwable ignored) {
            return ItemRegistry.get(s).asItemStack();
        }
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SHOOT;
    }
}
