package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Random;

public class ForcePullMechanic extends AbstractMechanic {
    @SerializedName(value = "spread", alternate = {"s"})
    Resolvable<Double> spread = Resolvable.literal(0.0);

    @SerializedName(value = "vspread", alternate = {"spreadv", "vs"})
    Resolvable<Double> vspread;

    transient private final Random random = new Random();

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets == null) return ExecutionResult.NULL;

        double hSpread = spread.resolve(tree);
        double vSpread = vspread != null ? vspread.resolve(tree) : hSpread;

        var casterEntity = tree.caster();

        double cx = casterEntity.position().x;
        double cy = casterEntity.position().y;
        double cz = casterEntity.position().z;

        for (Target target : targets) {
            Entity e = target.getEntity();
            if (e == null) continue;

            double dx = (random.nextDouble() * 2 - 1) * hSpread;
            double dz = (random.nextDouble() * 2 - 1) * hSpread;
            double dy = (random.nextDouble() * 2 - 1) * vSpread;

            double tx = cx + dx;
            double ty = cy + dy;
            double tz = cz + dz;

            e.teleportTo(tx, ty, tz);
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.FORCEPULL;
    }
}
