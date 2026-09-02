package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Set;

public class BiomeTypeCondition extends AbstractCondition {
    @SerializedName(value = "type", alternate = "t")
    Set<ResourceLocation> tags;

    @SerializedName(value = "exact", alternate = "e")
    boolean exact = true;

    public boolean test(SkillTree tree, Target target) {
        BlockPos pos = target.getBlockPos();
        var biome = tree.level().getBiome(pos);
        var key = biome.tags();
        for (TagKey<Biome> tagKey : key.toList()) {
            var id = tagKey.location();

            if (exact)
                return tags.contains(id);
            else {
                for (ResourceLocation tag : tags) {
                    if (tag.toString().toLowerCase().contains(id.getPath()))
                        return true;
                }
            }
        }

        return false;
    }
}