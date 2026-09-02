package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.util.BukkitIdConverter;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public class EntityTypeCondition extends AbstractCondition {
    @SerializedName(value = "subtype", alternate = "t")
    String type;

    public boolean test(SkillTree tree, Target target) {
        var tid = BukkitIdConverter.mobEffect(type).orElseGet(() -> ResourceLocation.parse(type.toLowerCase(Locale.ROOT).replace(" ", "_")));
        return target.isEntity() && target.getEntity().getType().builtInRegistryHolder().key().location().equals(tid);
    }
}
