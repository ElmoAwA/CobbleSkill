package de.tomalbrc.skillcore.impl.target;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;

import java.util.List;
import java.util.stream.Collectors;

public class EntitiesInRadiusTargeter extends AbstractTargeter {
    Resolvable<Double> radius = Resolvable.literal(5.);

    @Override
    public List<Target> find(SkillTree tree) {
        return tree.getNearbyEntities(radius.resolve(tree)).stream()
                .map(Target::of)
                .collect(Collectors.toList());
    }
}