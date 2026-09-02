package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class SignalMechanic extends AbstractMechanic {
    @SerializedName(value = "signal", alternate = {"s"})
    Resolvable<String> signal = Resolvable.literal("ping");

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                if (target.getEntity() != null) {
                    var o = target.getEntity().overlay();
                    if (o != null) o.onSignal(tree.caster(), signal.resolve(tree, target));
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.SIGNAL;
    }
}