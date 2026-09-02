package de.tomalbrc.skillcore.impl.target;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import java.util.List;
import java.util.stream.Collectors;

public class TrackedTargeter extends AbstractTargeter {
    @Override
    public List<Target> find(SkillTree tree) {
        return PlayerLookup.tracking(tree.caster()).stream()
                .map(Target::of)
                .collect(Collectors.toList());
    }
}
