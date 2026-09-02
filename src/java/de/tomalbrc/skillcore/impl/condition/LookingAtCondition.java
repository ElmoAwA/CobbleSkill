package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.phys.Vec3;

public class LookingAtCondition extends AbstractCondition {
    public boolean test(SkillTree tree, Target target) {
        Vec3 dir = tree.caster().getLookAngle().normalize();
        Vec3 to = target.getPosition().subtract(tree.caster().position()).normalize();
        return dir.dot(to) >= 0.98;
    }
}
