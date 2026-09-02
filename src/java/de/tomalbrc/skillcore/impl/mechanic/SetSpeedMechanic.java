package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.List;

public class SetSpeedMechanic extends AbstractMechanic {
    @SerializedName(value = "speed", alternate = {"a", "amount", "m", "multiplier", "s", "v", "value"})
    Resolvable<Double> speed = Resolvable.literal(1.);

    @SerializedName(value = "subtype", alternate = "t")
    String type = "walk";

    @Override
    public ExecutionResult execute(SkillTree tree) {
        final boolean fly = type.equalsIgnoreCase("fly");

        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                if (target.getEntity().isAlive()) {
                    final LivingEntity living = target.getEntity().asLivingEntity();
                    SkillCore.SERVER.execute(() -> {
                        var attr = fly ? living.getAttribute(Attributes.FLYING_SPEED) : living.getAttribute(Attributes.MOVEMENT_SPEED);
                        if (attr != null) {
                            var resolved = speed.resolve(tree, target);
                            attr.setBaseValue(resolved * (fly ? 0.2 : 0.1));
                        }
                    });
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SETSPEED;
    }
}