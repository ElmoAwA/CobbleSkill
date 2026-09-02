package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;

import java.util.List;

public class LightningMechanic extends AbstractMechanic {
    @SerializedName(value = "amount", alternate = {"a"})
    Resolvable<Integer> amount = Resolvable.literal(1);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                var b = EntityType.LIGHTNING_BOLT.create(tree.level(), null, target.getBlockPos(), MobSpawnType.TRIGGERED, false, false);
                if (b != null) {
                    b.moveTo(target.getPosition());
                    SkillCore.SERVER.execute(() -> tree.level().addFreshEntity(b));
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.LIGHTNING;
    }
}