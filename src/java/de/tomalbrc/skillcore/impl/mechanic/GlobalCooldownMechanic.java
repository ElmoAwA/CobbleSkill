package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;

public class GlobalCooldownMechanic extends AbstractMechanic {
    @SerializedName(value = "ticks", alternate = {"t"})
    Resolvable<Integer> ticks = Resolvable.literal(20);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        tree.caster().setGlobalCooldown(ticks.resolve(tree, Target.of(tree.caster())));
        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.GLOBAL_COOLDOWN;
    }
}