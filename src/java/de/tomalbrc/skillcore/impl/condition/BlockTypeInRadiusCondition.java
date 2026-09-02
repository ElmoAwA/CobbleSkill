package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.world.phys.Vec3;

public class BlockTypeInRadiusCondition extends BlockTypeCondition {
    @SerializedName(value = "amount", alternate = "a") int amount;
    @SerializedName(value = "radius", alternate = "r") double radius;

    public boolean test(SkillTree tree, Target target) {
        int cnt = 0;
        for (int x = (int) Math.floor(target.getBlockPos().getX() - radius); x <= (int) Math.ceil(target.getBlockPos().getX() + radius); x++)
            for (int y = (int) Math.floor(target.getBlockPos().getY() - radius); y <= (int) Math.ceil(target.getBlockPos().getY() + radius); y++)
                for (int z = (int) Math.floor(target.getBlockPos().getZ() - radius); z <= (int) Math.ceil(target.getBlockPos().getZ() + radius); z++)
                    if (super.test(tree, Target.of(tree.level(), new Vec3(x,y,z)))) ++cnt;

        return cnt >= amount;
    }
}
