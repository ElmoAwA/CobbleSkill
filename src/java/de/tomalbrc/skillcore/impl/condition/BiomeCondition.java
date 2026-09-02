package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public class BiomeCondition extends AbstractCondition {
    @SerializedName(value = "biome", alternate = "b")
    Set<ResourceLocation> biomes;

    @SerializedName(value = "exact", alternate = "e")
    boolean exact = true;

    public boolean test(SkillTree tree, Target target) {
        BlockPos pos = target.getBlockPos();
        var key = tree.level().getBiome(pos).unwrapKey();
        ResourceLocation id = null;
        if (key.isPresent()) {
            id = key.orElseThrow().location();
        }

        if (id == null)
            return false;
        else if (exact)
            return biomes.contains(id);
        else {
            for (ResourceLocation biome : biomes) {
                if (biome.toString().toLowerCase().contains(id.getPath()))
                    return true;
            }
        }

        return false;
    }
}