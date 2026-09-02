package de.tomalbrc.skillcore.impl.target;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.world.entity.Mob;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MobsInRadiusTargeter extends AbstractTargeter {
    Resolvable<Double> radius = Resolvable.literal(5.);

    @Override
    public List<Target> find(SkillTree tree) {
        return tree.getNearbyEntities(radius.resolve(tree)).stream()
                .map(x -> x instanceof Mob livingEntity ? Target.of(livingEntity) : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}