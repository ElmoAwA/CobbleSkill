package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.phys.Vec3;

public class FieldOfViewCondition extends AbstractCondition {
    private final double angle;

    FieldOfViewCondition(double angle) {
        this.angle = angle;
    }

    public boolean test(SkillTree tree, Target target) {
        Vec3 look = tree.caster().getLookAngle().normalize();
        Vec3 to = target.getPosition().subtract(tree.caster().position()).normalize();
        double dot = look.dot(to);
        double deg = Math.acos(dot) * 180 / Math.PI;
        return deg <= angle;
    }
}