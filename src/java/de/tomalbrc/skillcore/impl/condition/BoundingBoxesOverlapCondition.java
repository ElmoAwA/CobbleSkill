package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.phys.AABB;

public class BoundingBoxesOverlapCondition extends AbstractCondition {
    @SerializedName(value = "shiftforward", alternate = {"so", "forwardoffset", "fo", "forward", "f"})
    double shiftforward;

    public boolean test(SkillTree tree, Target target) {
        AABB a = tree.caster().getBoundingBox();
        AABB b = target.getEntity().getBoundingBox();

        if (shiftforward != 0) {
            a = a.move(tree.caster().getLookAngle().normalize().scale(shiftforward));
        }

        return a.intersects(b);
    }
}