package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.RangedValue;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Slime;

public class SizeCondition extends AbstractCondition {
    @SerializedName(value = "size", alternate = "s")
    RangedValue size;

    public boolean test(SkillTree tree, Target target) {
        var s = 0;
        if (target.getEntity() instanceof Slime slime)
            s = slime.getSize();
        else if (target.getEntity() instanceof Phantom slime)
            s = slime.getPhantomSize();
        else return false;

        return size.isWithin(s);
    }
}
