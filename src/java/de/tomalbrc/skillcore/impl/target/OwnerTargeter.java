package de.tomalbrc.skillcore.impl.target;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.entity.OwnableEntity;

import java.util.List;

public class OwnerTargeter extends AbstractTargeter {
    @Override
    public List<Target> find(SkillTree tree) {
        if (tree.caster() instanceof OwnableEntity ownableEntity) {
            return List.of(Target.of(ownableEntity.getOwner()));
        }

        return List.of();
    }
}
