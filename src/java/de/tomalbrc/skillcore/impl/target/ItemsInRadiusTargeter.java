package de.tomalbrc.skillcore.impl.target;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemsInRadiusTargeter extends AbstractTargeter {
    @SerializedName(value = "radius", alternate = "r")
    Resolvable<Double> radius = Resolvable.literal(5.);

    @Override
    public List<Target> find(SkillTree tree) {
        return tree.getNearbyEntities(radius.resolve(tree)).stream()
                .map(x -> x instanceof ItemEntity entity ? Target.of(entity) : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}