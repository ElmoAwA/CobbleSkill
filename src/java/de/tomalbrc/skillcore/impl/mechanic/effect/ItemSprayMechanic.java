package de.tomalbrc.skillcore.impl.mechanic.effect;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import de.tomalbrc.skillcore.mixin.accessor.ItemEntityAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Random;

public class ItemSprayMechanic extends AbstractMechanic {
    @SerializedName(value = "items", alternate = {"item", "i"})
    Resolvable<String> items = Resolvable.literal("iron_sword");

    @SerializedName(value = "amount", alternate = {"a"})
    Resolvable<Integer> amount = Resolvable.literal(10);

    @SerializedName(value = "duration", alternate = {"d"})
    Resolvable<Integer> duration = Resolvable.literal(20);

    @SerializedName(value = "radius", alternate = {"r"})
    Resolvable<Double> radius = Resolvable.literal(0.0);

    @SerializedName(value = "velocity", alternate = {"v", "force", "f"})
    Resolvable<Double> velocity = Resolvable.literal(1.0);

    @SerializedName(value = "yvelocity", alternate = {"yv", "yforce", "yf"})
    Resolvable<Double> yVelocity = Resolvable.nullable();

    @SerializedName(value = "yoffset", alternate = {"yo", "y"})
    Resolvable<Double> yOffset = Resolvable.literal(1.0);

    @SerializedName(value = "allowpickup", alternate = {"ap"})
    Resolvable<Boolean> allowPickup = Resolvable.literal(false);

    @SerializedName(value = "gravity", alternate = {"g"})
    Resolvable<Boolean> gravity = Resolvable.literal(true);

    @SerializedName(value = "audience")
    Resolvable<String> audience = Resolvable.literal("world");

    transient Random random = new Random();

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        int amt = amount.resolve(tree);
        double rad = radius.resolve(tree);
        double vel = velocity.resolve(tree);
        double yv = yVelocity.resolve(tree) != null ? yVelocity.resolve(tree) : vel;
        double yo = yOffset.resolve(tree);
        boolean pickup = allowPickup.resolve(tree);
        boolean grav = gravity.resolve(tree);

        for (Target target : targets) {
            var loc = target.getEntity().position();
            var level = (ServerLevel) target.getEntity().level();

            for (int i = 0; i < amt; i++) {
                var mcItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(items.resolve(tree, target)));

                double ox = (random.nextDouble() * 2 - 1) * rad;
                double oz = (random.nextDouble() * 2 - 1) * rad;

                double px = loc.x + ox;
                double py = loc.y + yo;
                double pz = loc.z + oz;

                double vx = (random.nextDouble() * 2 - 1) * vel;
                double vz = (random.nextDouble() * 2 - 1) * vel;
                double vy = (random.nextDouble() * 2 - 1) * yv;

                ItemEntity entity = new ItemEntity(level, px, py, pz, new ItemStack(mcItem));
                entity.setDeltaMovement(vx, vy, vz);
                entity.setNoGravity(!grav);
                ((ItemEntityAccessor) entity).setAge(6000 - duration.resolve(tree, target));
                if (!pickup) entity.setNeverPickUp();
                SkillCore.SERVER.execute(() -> level.addFreshEntity(entity));

                // todo: virtual option for !pickup
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.ITEMSPRAY;
    }
}
