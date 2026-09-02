package de.tomalbrc.skillcore.impl.mechanic.model;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import net.minecraft.resources.ResourceLocation;

public class StateMechanic extends AbstractMechanic {
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
                var overlay = target.getEntity().overlay();
                if (overlay !=  null && overlay.customModel(modelid) != null) {
                    var sm = overlay.customModel(modelid).stateMachineHandler();
                    if (sm != null)
                        sm.playAnimation(10, state, false);
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