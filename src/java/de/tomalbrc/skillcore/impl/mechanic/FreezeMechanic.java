package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import de.tomalbrc.skillcore.util.RangedValue;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class FreezeMechanic extends AbstractMechanic {
    @SerializedName(value = "ticks", alternate = {"t", "duration", "d"})
    Resolvable<RangedValue> ticks;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                if (target.getEntity() != null) {
                    target.getEntity().setTicksFrozen(ticks.resolve(tree, target).getAsInteger());
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.FREEZE;
    }
}