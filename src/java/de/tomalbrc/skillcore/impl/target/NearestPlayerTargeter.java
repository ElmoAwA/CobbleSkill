package de.tomalbrc.skillcore.impl.target;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NearestPlayerTargeter extends AbstractTargeter {
    @SerializedName(value = "radius", alternate = "r")
    Resolvable<Double> radius = Resolvable.literal(5.);

    @Override
    public List<Target> find(SkillTree tree) {
        return tree.getNearbyEntities(radius.resolve(tree)).stream()
                .map(x -> x instanceof Player livingEntity ? Target.of(livingEntity) : null)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(p -> p.getPosition().distanceTo(tree.caster().position())))
                .limit(1)
                .collect(Collectors.toList());
    }
}