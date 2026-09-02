package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class TargetWithinCondition extends AbstractCondition {
    @SerializedName(value = "radius", alternate = {"r", "d", "distance"})
    double r;

    public boolean test(SkillTree tree, Target target) {
        LivingEntity t = ((Mob) tree.caster()).getTarget();
        if (t == null) return false;

        return Math.sqrt(t.distanceToSqr(target.getPosition())) <= r;
    }
}
