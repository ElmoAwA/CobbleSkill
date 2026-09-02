package de.tomalbrc.skillcore.impl.target;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.entity.animal.Wolf;

import java.util.List;

public class WolfOwnerTargeter extends AbstractTargeter {
    @Override
    public List<Target> find(SkillTree tree) {
        if (tree.caster() instanceof Wolf wolf && wolf.getOwner() != null) {
            return List.of(Target.of(wolf.getOwner()));
        }

        return List.of();
    }
}