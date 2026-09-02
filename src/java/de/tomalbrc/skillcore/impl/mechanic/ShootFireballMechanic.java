package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class ShootFireballMechanic extends AbstractMechanic {
    @SerializedName(value = "yield", alternate = {"strength", "y", "s"})
    Resolvable<Double> yield = Resolvable.literal(1.0);

    @SerializedName(value = "velocity", alternate = {"v"})
    Resolvable<Double> velocity = Resolvable.literal(1.0);

    @SerializedName(value = "fireticks", alternate = {"ft"})
    Resolvable<Integer> fireTicks = Resolvable.literal(0);

    @SerializedName(value = "incendiary", alternate = {"i"})
    Resolvable<Boolean> incendiary = Resolvable.literal(false);

    @SerializedName(value = "charged", alternate = {"c"})
    Resolvable<Boolean> charged = Resolvable.literal(false);

    @SerializedName(value = "fromorigin", alternate = {"fo"})
    Resolvable<Boolean> fromOrigin = Resolvable.literal(false);

    @SerializedName(value = "playsound", alternate = {"ps"})
    Resolvable<Boolean> playSound = Resolvable.literal(false);

    @SerializedName(value = "smallfireball", alternate = {"small", "sml"})
    Resolvable<Boolean> small = Resolvable.literal(false);

    @SerializedName(value = "type", alternate = {"t"})
    Resolvable<String> type = Resolvable.literal("SMALL");

    @SerializedName(value = "item", alternate = {"material"})
    Resolvable<String> itemMaterial = Resolvable.literal("");

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        Entity casterEntity = tree.caster();
        if (casterEntity == null) return ExecutionResult.NULL;

        ServerLevel level = tree.level();

        // TODO: impl
        double basePower = yield.resolve(tree);
        int fire = fireTicks.resolve(tree);
        boolean inc = incendiary.resolve(tree);
        boolean isCharged = charged.resolve(tree);
        boolean useOrigin = fromOrigin.resolve(tree);
        double vel = velocity.resolve(tree);
        boolean doPlaySound = playSound.resolve(tree);
        String typeName = type.resolve(tree).toUpperCase();
        String itemMat = itemMaterial.resolve(tree);

        LivingEntity shooter = casterEntity instanceof LivingEntity ? (LivingEntity) casterEntity : null;

        for (Target t : targets) {
            Entity targetEntity = t.getEntity();
            if (targetEntity == null) continue;

            Vec3 originVec = new Vec3(casterEntity.getX(), casterEntity.getEyeY(), casterEntity.getZ());

            double ox = originVec.x;
            double oy = originVec.y;
            double oz = originVec.z;

            double tx = targetEntity.getX();
            double ty = targetEntity.getEyeY();
            double tz = targetEntity.getZ();

            Vec3 dir = new Vec3(tx - ox, ty - oy, tz - oz);
            if (dir.length() == 0) dir = casterEntity.getLookAngle();
            dir = dir.normalize();

            Entity proj = null;

            switch (typeName) {
                case "SMALL": {
                    Optional<SmallFireball> created = Optional.ofNullable(EntityType.SMALL_FIREBALL.create(level));
                    if (created.isPresent()) {
                        proj = created.get();
                    }
                    break;
                }
                case "LARGE": {
                    Optional<LargeFireball> created = Optional.ofNullable(EntityType.FIREBALL.create(level));
                    if (created.isPresent()) proj = created.get();
                    break;
                }
                case "WITHER": {
                    Optional<WitherSkull> created = Optional.ofNullable(EntityType.WITHER_SKULL.create(level));
                    if (created.isPresent()) proj = created.get();
                    break;
                }
                case "DRAGON": {
                    Optional<DragonFireball> created = Optional.ofNullable(EntityType.DRAGON_FIREBALL.create(level));
                    if (created.isPresent()) proj = created.get();
                    break;
                }
                case "ITEM": {
                    ItemEntity created = EntityType.ITEM.create(level);
                    if (created != null) {
                        Item item = Items.BLAZE_POWDER;
                        if (itemMat != null && !itemMat.isEmpty()) {
                            ResourceLocation rl = ResourceLocation.parse(itemMat);
                            Optional<Item> opt = BuiltInRegistries.ITEM.getOptional(rl);
                            if (opt.isPresent()) item = opt.get();
                        }
                        created.setItem(item.getDefaultInstance());
                        proj = created;
                    }
                    break;
                }
                case "NORMAL":
                default: {
                    Optional<SmallFireball> created = Optional.ofNullable(EntityType.SMALL_FIREBALL.create(level));
                    if (created.isPresent()) proj = created.get();
                    break;
                }
            }

            if (proj == null) continue;

            proj.setPos(ox, oy, oz);
            proj.setDeltaMovement(dir.scale(vel));

            if (shooter != null && proj instanceof Projectile projectile) {
                projectile.setOwner(shooter);
            }

            Entity finalProj = proj;
            SkillCore.SERVER.execute(() -> level.addFreshEntity(finalProj));

            if (doPlaySound) level.playSound(null, ox, oy, oz, SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 1f, 1f);
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SHOOTFIREBALL;
    }
}
