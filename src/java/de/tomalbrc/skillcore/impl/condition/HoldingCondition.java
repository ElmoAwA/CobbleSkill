package de.tomalbrc.skillcore.impl.condition;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import de.tomalbrc.skillcore.registry.ItemRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.regex.Pattern;

public class HoldingCondition extends AbstractCondition {
    @SerializedName(value = "material", alternate = {"m", "type", "t", "item", "i", "mat", "types"})
    String material;
    @SerializedName(value = "strict", alternate = {"exact", "e"})
    boolean strict = false;
    @SerializedName(value = "vanillaonly", alternate = {"vanilla"})
    boolean vanillaonly = false;

    @Override
    public boolean test(SkillTree tree, Target target) {
        if (material == null || material.isEmpty() || !target.isEntity() || target.getEntity().asLivingEntity() == null) return false;

        String matLower = material.toLowerCase(Locale.ROOT);
        boolean isTag = matLower.startsWith("#");
        String patternString = isTag ? matLower.substring(1) : matLower;

        patternString = patternString.replace("*", ".*");
        Pattern pattern = Pattern.compile(patternString);

        for (ItemStack stack : target.getEntity().asLivingEntity().getHandSlots()) {
            if (stack.isEmpty()) continue;

            if (isTag) {
                if (stack.getTags().anyMatch(tag -> pattern.matcher(tag.location().toString()).matches())) {
                    return true;
                }
            } else if (!vanillaonly) {
                CompoundTag nbt = stack.get(DataComponents.CUSTOM_DATA).copyTag();
                if (nbt.contains("SkillCoreItem")) {
                    String skillCoreItemId = nbt.getString("SkillCoreItem").toLowerCase(Locale.ROOT);
                    if (pattern.matcher(skillCoreItemId).matches()) return true;
                }
            }

            ResourceLocation id = ResourceLocation.tryParse(matLower);

            if (matLower.contains("*")) {
                if (pattern.matcher(stack.getItem().builtInRegistryHolder().key().location().toString()).matches()) return true;
            } else if (strict) {
                if (stack.getItem().builtInRegistryHolder().key().location().equals(id)) return true;
            } else {
                if (stack.getItem().builtInRegistryHolder().key().location().toString().contains(matLower)) return true;
            }
        }

        return false;
    }
}
