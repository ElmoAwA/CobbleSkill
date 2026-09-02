package de.tomalbrc.skillcore.impl.target;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.overlay.EntityOverlay;
import de.tomalbrc.skillcore.api.target.Target;

import java.util.ArrayList;
import java.util.List;

public class ModelPassengersTargeter extends AbstractTargeter {
    @SerializedName(value = "modelid", alternate = {"m", "mid", "model"})
    String modelid;

    @SerializedName(value = "seat", alternate = {"p", "pbone"})
    List<String> boneName;

    @Override
    public List<Target> find(SkillTree tree) {
        List<Target> res = new ArrayList<>();
        EntityOverlay o = tree.caster().overlay();
        if (o != null && o.customModel(modelid) != null) {
            var holder = o.customModel(modelid);
            for (String s : boneName) {
                var passenger = holder.getPassenger(s);
                if (passenger != null) {
                    res.add(Target.of(passenger));
                }
            }
        }

        return res;
    }
}