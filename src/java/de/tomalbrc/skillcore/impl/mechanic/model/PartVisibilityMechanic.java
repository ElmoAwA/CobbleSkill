package de.tomalbrc.skillcore.impl.mechanic.model;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.mechanic.AbstractMechanic;
import net.minecraft.resources.ResourceLocation;

public class PartVisibilityMechanic extends AbstractMechanic {
    @SerializedName(value = "modelid", alternate = {"m", "mid", "model"})
    String modelid;
    @SerializedName(value = "partid", alternate = {"p", "pid", "part"})
    String partid = "";
    @SerializedName(value = "visibility", alternate = {"v", "visible"})
    boolean visibility;
    @SerializedName(value = "exactmatch", alternate = {"em", "exact", "match"})
    boolean exactmatch = true;
    @SerializedName(value = "child", alternate = {"c"})
    boolean child;

    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (tree.getCurrentTargets() != null) {
            for (Target target : tree.getCurrentTargets()) {
                SkillCore.SERVER.execute(() -> {
                    var overlay = target.getEntity().overlay();
                    if (overlay != null && overlay.customModel(modelid) != null) {
                        var sm = overlay.customModel(modelid);
                        if (sm != null) {
                            sm.setForceHidden(partid, visibility, exactmatch, child);
                        }
                    }
                });
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.PART_VISIBILITY;
    }
}