package de.tomalbrc.skillcore.impl.condition;

import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HasItemCondition extends AbstractCondition {
    //private final ItemStack sample;
    private final int min;

    HasItemCondition(ItemStack s, int min) {
        //this.sample = s;
        this.min = min;
    }

    public boolean test(SkillTree tree, Target target) {
        if (!(target.getEntity() instanceof Player p)) return false;
        int cnt = 0;
        //for (ItemStack it : p.getInventory()) if (ItemStack.isSameItem(it, sample)) cnt++;
        return cnt >= min;
    }
}
