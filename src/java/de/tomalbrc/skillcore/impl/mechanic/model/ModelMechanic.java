package de.tomalbrc.skillcore.impl.mechanic.model;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import de.tomalbrc.skillcore.impl.mechanic.model.holder.LivingEntityHolder;
import de.tomalbrc.skillcore.registry.Models;
import net.minecraft.resources.ResourceLocation;

public class ModelMechanic extends AbstractMechanic {
    @SerializedName(value = "modelid", alternate = {"m", "mid", "model"})
    String modelid;

    @SerializedName(value = "remove", alternate = {"r"})
    boolean remove;

    @SerializedName(value = "invisible", alternate = {"invis", "i"})
    boolean invisible = true;

    @SerializedName(value = "killowner", alternate = {"ko"})
    boolean killowner = false;

    @SerializedName(value = "usestatemachine", alternate = {"usm", "state", "statemachine"})
    boolean usestatemachine = false;


    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (tree.getCurrentTargets() != null) {
            for (Target target : tree.getCurrentTargets()) {
                var overlay = target.getEntity().overlay();
                if (overlay != null) {
                    SkillCore.SERVER.execute(() -> {
                        if (remove && overlay.customModel(modelid) != null) {
                            overlay.removedCustomModel(modelid);
                            if (killowner) target.getEntity().discard();
                        } else if (overlay.customModel(modelid) == null) {
                            Model m = Models.getModel(modelid);

                            if (m == null) {
                                SkillCore.LOGGER.error("Can not find model: {}", modelid);
                                return;
                            }

                            var l = new LivingEntityHolder<>(target.getEntity().asLivingEntity(), m);
                            if (usestatemachine)
                                l.setupStateMachine();
                            overlay.addCustomModel(l, modelid, true, invisible);
                        }
                    });
                }
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.MODEL;
    }
}