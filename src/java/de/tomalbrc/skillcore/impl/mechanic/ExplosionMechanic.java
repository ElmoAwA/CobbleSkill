package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExplosionMechanic extends AbstractMechanic {
    @SerializedName(value = "yield", alternate = {"y"})
    Resolvable<Double> yield = Resolvable.literal(0.0135);

    @SerializedName(value = "blockdamage", alternate = {"bd"})
    Resolvable<Boolean> blockDamage = Resolvable.literal(false);

    @SerializedName(value = "fire", alternate = {"f", "ft"})
    Resolvable<Boolean> fire = Resolvable.literal(false);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null || targets.isEmpty()) return ExecutionResult.NULL;

        Level level = tree.level();

        boolean doBlockDamage = blockDamage.resolve(tree);
        boolean doFire = fire.resolve(tree);
        float power = yield.resolve(tree).floatValue();

        Level.ExplosionInteraction interaction = doBlockDamage ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE;

        for (Target t : targets) {
            Entity targetEntity = t.getEntity();
            if (targetEntity == null) continue;

            double x = targetEntity.getX();
            double y = targetEntity.getY();
            double z = targetEntity.getZ();
            SkillCore.SERVER.execute(() -> level.explode(null, x, y, z, power, doFire, interaction));
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.EXPLOSION;
    }
}
