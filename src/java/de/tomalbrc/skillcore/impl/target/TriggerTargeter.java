package de.tomalbrc.skillcore.impl.target;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;

import java.util.List;

public class TriggerTargeter extends AbstractTargeter {
    @Override
    public List<Target> find(SkillTree tree) {
        return List.of(tree.trigger());
    }
}