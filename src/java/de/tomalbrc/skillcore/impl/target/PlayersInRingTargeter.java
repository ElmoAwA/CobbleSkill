package de.tomalbrc.skillcore.impl.target;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PlayersInRingTargeter extends AbstractTargeter {
    @SerializedName(value = "min", alternate = "minrange")
    Resolvable<Double> min = Resolvable.literal(1.);
    @SerializedName(value = "max", alternate = "maxrange")
    Resolvable<Double> max = Resolvable.literal(5.);

    @Override
    public List<Target> find(SkillTree tree) {
        var maxr = max.resolve(tree);
        var minr = min.resolve(tree);
        return tree.getNearbyEntities(maxr).stream()
                .filter(entity -> entity instanceof Player)
                .filter(entity -> {
                    double distance = entity.position().distanceTo(tree.caster().position());
                    return distance >= minr && distance <= maxr;
                })
                .map(Target::of)
                .collect(Collectors.toList());
    }
}