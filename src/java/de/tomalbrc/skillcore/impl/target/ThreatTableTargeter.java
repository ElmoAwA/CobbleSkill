package de.tomalbrc.skillcore.impl.target;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class ThreatTableTargeter extends AbstractTargeter {
    @Override
    public List<Target> find(SkillTree tree) {
        var t = tree.caster().asLivingEntity();
        if (t != null ) {
            var table = t.getThreatTable();
            var entities = table.getAll();
            List<Target> targets = new ArrayList<>(entities.size());
            for (Entity entity : entities) {
                targets.add(Target.of(entity));
            }
            return targets;
        }

        return List.of();
    }
}