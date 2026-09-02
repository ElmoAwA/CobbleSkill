package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.overlay.EntityOverlay;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class ModelHasPassengersCondition extends AbstractCondition {
    enum Mode {
        AND,
        OR
    }

    @SerializedName(value = "modelid", alternate = {"m", "mid", "model"})
    String modelid;

    @SerializedName(value = "seat", alternate = {"p", "pbone"})
    List<String> boneName;

    @SerializedName("mode")
    Mode mode = Mode.AND;

    public boolean test(SkillTree tree, Target target) {
        EntityOverlay<? extends Entity> o = tree.caster().overlay();
        if (o != null && o.customModel(modelid) != null) {
            boolean has = false;
            for (String s : boneName) {
                var passenger = o.customModel(modelid).getPassenger(s);
                if (mode == Mode.OR && passenger != null) {
                    return true;
                } else if (mode == Mode.AND && passenger == null) {
                    return false;
                } else {
                    has = has || passenger != null;
                }
            }

            return has;
        }

        return false;
    }
}