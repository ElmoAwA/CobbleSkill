package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class TargetNotWithinCondition extends AbstractCondition {
    @SerializedName(value = "radius", alternate = {"r", "d", "distance"})
    RangedValue r;

    public boolean test(SkillTree tree, Target target) {
        if (!(tree.caster() instanceof Mob mob))
            return true;

        LivingEntity t = mob.getTarget();
        if (t == null) return true;

        return !r.isWithin(Math.sqrt(t.distanceToSqr(target.getPosition())));
    }
}
