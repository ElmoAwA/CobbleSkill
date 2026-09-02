package de.tomalbrc.skillcore.impl.mechanic.aura;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.core.SkillEngine;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;

public class AuraRemoveMechanic extends AbstractMechanic {
    @SerializedName(value = "auraname", alternate = {"aura", "b", "buff", "buffname", "debuff", "debuffname", "n", "name"})
    Resolvable<String> auraName;

    @SerializedName(value = "stacks", alternate = "s")
    int stacks;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        for (Target target : tree.getCurrentTargets()) {
            // TODO: aura stacking
            SkillEngine.getInstance().auraManager().remove(target.getEntity(), auraName.resolve(tree, target));
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.AURAREMOVE;
    }
}
