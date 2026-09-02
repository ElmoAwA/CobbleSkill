package de.tomalbrc.skillcore.impl.target;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ModelPartTargeter extends AbstractTargeter {
    @SerializedName(value = "modelid", alternate = {"model", "mid", "m"})
    String modelid;
    @SerializedName(value = "partid", alternate = {"part", "pid", "p"})
    String partid;

    double x = 0;
    double y = 0;
    double z = 0;

    @SerializedName(value = "location", alternate = {"loc", "l", "coordinates", "c"})
    Vec3 location;

    @SerializedName(value = "exactmatch", alternate = {"exact", "match", "em"})
    boolean exactmatch;

    // TODO: scale
    @SerializedName(value = "scale", alternate = {"s", "sc"})
    double scale;

    @Override
    public List<Target> find(SkillTree tree) {
        var o = tree.caster().overlay();
        if (o != null) {
            var model = o.customModel(modelid);
            if (model != null) {
                var bone = model.getBone(partid, exactmatch);
                if (bone != null) {
                    var pos = model.worldPos(bone, location != null ? location : new Vec3(x, y, z));
                    return List.of(Target.of(tree.level(), pos));
                }
            }
        }

        return List.of();
    }
}
