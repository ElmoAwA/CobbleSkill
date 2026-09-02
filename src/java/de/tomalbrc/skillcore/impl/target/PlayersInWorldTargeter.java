package de.tomalbrc.skillcore.impl.target;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

import java.util.List;
import java.util.stream.Collectors;

public class PlayersInWorldTargeter extends AbstractTargeter {
    @Override
    public List<Target> find(SkillTree tree) {
        return tree.caster().level().players().stream()
                .map(Target::of)
                .collect(Collectors.toList());
    }
}