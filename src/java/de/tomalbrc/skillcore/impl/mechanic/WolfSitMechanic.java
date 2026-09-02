package de.tomalbrc.skillcore.impl.mechanic;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;

import java.util.List;

public class WolfSitMechanic extends AbstractMechanic {
    @SerializedName(value = "sit", alternate = {"state", "value"})
    protected boolean state = true;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        List<Target> targets = tree.getCurrentTargets();
        for (Target target : targets) {
            if (target.getEntity() instanceof Wolf wolf) {
                wolf.setOrderedToSit(state);
            }
        }
        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.WOLF_SIT;
    }
}
