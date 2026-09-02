package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class HealMechanic extends AbstractMechanic {
    @SerializedName(value = "amount", alternate = {"a"})
    Resolvable<Integer> amount;
    @SerializedName(value = "overheal", alternate = {"oh"})
    boolean overheal;
    @SerializedName(value = "maxoverheal", alternate = {"maxabsorb", "maxshield", "mo", "ma", "ms"})
    Resolvable<Integer> maxoverheal;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        if (targets != null) {
            for (Target target : targets) {
                if (target.getEntity() instanceof LivingEntity living) {
                    living.heal(amount.resolve(tree, target));
                    // TODO: overheal, maxoverheal
                    //living.setAbsorptionAmount(0);
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.HEAL;
    }
}