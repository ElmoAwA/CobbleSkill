package de.tomalbrc.skillcore.impl.mechanic.effect;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import de.tomalbrc.skillcore.impl.aura.SpinAura;
import de.tomalbrc.skillcore.impl.mechanic.aura.AbstractAuraMechanic;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class SpinMechanic extends AbstractAuraMechanic {
    public @SerializedName(value = "velocity", alternate = {"v"})
    float velocity = 18.f;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                var t = new SpinAura(tree.copyWith(target.getPosition(), List.of(target)), this, target, velocity);
                SkillEngine.getInstance().addAura(t);
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SPIN;
    }
}