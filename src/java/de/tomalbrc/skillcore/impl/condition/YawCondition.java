package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;
import net.minecraft.util.Mth;

public class YawCondition extends AbstractCondition {
    @SerializedName(value = "yaw", alternate = "y")
    RangedValue yaw = RangedValue.of(0.);
    @SerializedName(value = "clamp", alternate = "c")
    boolean clamp = true;
    public boolean test(SkillTree tree, Target target) {
        double py = target.getEntity().getYRot();
        if (clamp) py = Mth.clamp(py, 0., 360.);
        return yaw.isWithin(py);
    }
}
