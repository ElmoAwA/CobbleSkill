package de.tomalbrc.skillcore.impl.target;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

import java.util.List;

public class OriginTargeter extends AbstractTargeter {
    @SerializedName(value = "xoffset", alternate = "xo") float xoffset = 0;
    @SerializedName(value = "yoffset", alternate = "yo") float yoffset = 0;
    @SerializedName(value = "zoffset", alternate = "zo") float zoffset = 0;

    @Override
    public List<Target> find(SkillTree tree) {
        return List.of(Target.of(tree.level(), tree.origin().add(xoffset, yoffset, zoffset)));
    }
}