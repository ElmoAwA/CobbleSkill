package de.tomalbrc.skillcore.impl.mechanic;

import de.tomalbrc.skillcore.api.execution.ExecutionResult;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Items;

public class BoneMealMechanic extends AbstractMechanic {
    // TODO: clicked face option

    @Override
    public ExecutionResult execute(SkillTree tree) {
        if (tree.getCurrentTargets() != null) {
            for (Target target : tree.getCurrentTargets()) {
                BoneMealItem.growCrop(Items.BONE_MEAL.getDefaultInstance(), tree.level(), target.getBlockPos());
            }
        }

        return ExecutionResult.NULL;
    }

    @Override
    public ResourceLocation id() {
        return Mechanics.BONEMEAL;
    }
}
