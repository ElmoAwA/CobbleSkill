package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.item.ItemStack;

public class TriggerItemTypeCondition extends AbstractCondition {
    //private final ItemStack sample;

    TriggerItemTypeCondition(ItemStack s) {
        //this.sample = s;
    }

    // TODO: impl
    public boolean test(SkillTree tree, Target target) {
        //Object it = ctx.vars().get("triggerItem");
        //if (!(it instanceof ItemStack stack)) return false;
        //return ItemStack.isSameItem(stack, sample);
        return false;
    }
}
