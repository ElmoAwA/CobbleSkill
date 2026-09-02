package de.tomalbrc.skillcore.impl.target;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ThreatTablePlayersTargeter extends AbstractTargeter {
    @Override
    public List<Target> find(SkillTree tree) {
        var t = tree.caster().asLivingEntity().getThreatTable();
        if (t != null) {
            var entities = t.getAll();
            List<Target> targets = new ArrayList<>(entities.size());
            for (Entity entity : entities) {
                if (entity instanceof Player) targets.add(Target.of(entity));
            }
            return targets;
        }

        return List.of();
    }
}