package de.tomalbrc.skillcore.impl.target;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

import java.util.List;

public class VehicleTargeter extends AbstractTargeter {
    @Override
    public List<Target> find(SkillTree tree) {
        var v = tree.caster().getVehicle();
        return v != null ? List.of(Target.of(v)) : List.of();
    }
}