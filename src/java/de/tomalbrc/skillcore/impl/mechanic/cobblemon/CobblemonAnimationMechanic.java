package de.tomalbrc.skillcore.impl.mechanic.cobblemon;

import com.cobblemon.mod.common.entity.PosableEntity;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class CobblemonAnimationMechanic extends AbstractMechanic {
    @SerializedName(value = "modelid", alternate = {"m", "mid", "model"})
    String modelid;

    @SerializedName(value = "state", alternate = {"s"})
    String state;

    @SerializedName(value = "remove", alternate = {"r"})
    boolean remove;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (tree.getCurrentTargets() != null) {
            for (Target target : tree.getCurrentTargets()) {
                if (target.getEntity() instanceof PosableEntity posableEntity) {
                    posableEntity.playAnimation(state, List.of());
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.STATE;
    }
}