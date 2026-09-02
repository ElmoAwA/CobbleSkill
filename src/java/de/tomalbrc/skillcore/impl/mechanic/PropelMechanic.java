package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class PropelMechanic extends AbstractMechanic {
    @SerializedName(value = "velocity", alternate = {"magnitude", "v"})
    Resolvable<Double> velocity = Resolvable.literal(1.);

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        var t = targets.getFirst();

        // TODO: use caster dir vec * 2 if  diff.length  is 0

        var diff = t.getPosition().subtract(tree.caster().position());
        var next = diff.scale(velocity.resolve(tree, t));
        if (next.length() > 4) {
            next = next.normalize().scale(4);
        }
        tree.caster().setDeltaMovement(next);

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.PROPEL;
    }
}