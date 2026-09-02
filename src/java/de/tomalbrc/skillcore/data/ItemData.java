package de.tomalbrc.skillcore.data;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.SkillCoreComponents;
import de.tomalbrc.skillcore.util.BukkitIdConverter;
import de.tomalbrc.skillcore.util.TextUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public record ItemData(
        String identifier,
        @SerializedName(value = "Id", alternate = "Material") ResourceLocation item,
        @SerializedName("Display") String display, @SerializedName("Lore") List<String> lore,
        @SerializedName("CustomModelData") Integer customModelData,
        @SerializedName("MaxDurability") Integer maxDurability,
        @SerializedName("Enchantments") List<String> enchantments,
        @SerializedName("Options") ItemOptions options, @SerializedName("Group") String group,
        @SerializedName("CanBreak") List<String> canBreak,
        @SerializedName("CanPlaceOn") List<String> canPlaceOn,
        @SerializedName("Ammo") AmmoOptions ammo,
        @SerializedName("Skills") Set<String> skills
) {
    public ItemStack asItemStack() {
        ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.get(item));
        itemStack.set(DataComponents.ITEM_NAME, TextUtil.formatText(display));

        if (customModelData != null) {
            itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(customModelData));
        }

        if (lore != null) {
            var loreLines = lore.stream().map(TextUtil::formatText).toList();
            itemStack.set(DataComponents.LORE, new ItemLore(loreLines, loreLines));
        }

        if (enchantments != null) {
            var enchants = new ItemEnchantments.Mutable(itemStack.getEnchantments());

            for (String enchantment : enchantments) {
                enchantment = enchantment.trim();

                var e = enchantment.split(" ");
                var realEid = BukkitIdConverter.enchantment(e[0]).orElse(ResourceLocation.parse(e[0].toLowerCase(Locale.ROOT)));
                var enchant = SkillCore.SERVER.registryAccess().asGetterLookup().get(Registries.ENCHANTMENT, ResourceKey.create(Registries.ENCHANTMENT, realEid));
                enchant.ifPresent(x -> enchants.set(x, e.length > 1 ? Integer.parseInt(e[1]) : 1));
            }

            itemStack.set(DataComponents.ENCHANTMENTS, enchants.toImmutable());
        }

        if (maxDurability != null) {
            itemStack.set(DataComponents.MAX_DAMAGE, maxDurability);
        }

        if (skills != null) {
            var component = new SkillCoreComponents.Skillset(skills.stream().toList());
            itemStack.set(SkillCoreComponents.SKILLS, component);
        }

//        if (canPlaceOn != null) {
//            List<BlockPredicate> predicates = new ArrayList<>();
//            for (String s : canPlaceOn) {
//                predicates.add(new BlockPredicate())
//            }
//
//            itemStack.set(DataComponents.CAN_PLACE_ON, new AdventureModePredicate(List.of(), true));
//        }

        return itemStack;
    }

    record ItemOptions(
            @SerializedName("CancelDamage") boolean cancelDamage,
            @SerializedName("Destroy") boolean destroy,
            @SerializedName("DestroyOnDrop") boolean destroyOnDrop,
            @SerializedName("KeepOnDeath") boolean keepOnDeath,
            @SerializedName("PreventDropping") boolean preventDropping,
            @SerializedName("Permission") String permission,
            @SerializedName("Placeable") Boolean placeable,
            @SerializedName("PreventAnvil") boolean preventAnvil,
            @SerializedName("PreventSmithing") Boolean preventSmithing, // defaults to true for items with CMD
            @SerializedName("PreventCrafting") Boolean preventCrafting, // defaults to true for items with CMD
            @SerializedName("PreventEnchanting") boolean preventEnchanting,
            @SerializedName("SkillType") String skillType
    ) {

    }

    record AmmoOptions(@SerializedName("Enabled") boolean enabled,
                       @SerializedName("Bullet") String bullet,
                       @SerializedName("ClipSize") int clipSize,
                       @SerializedName("AmmoPerItem") int ammoPerItem) {
        public SkillCoreComponents.Ammo asComponent() {
            return new SkillCoreComponents.Ammo(bullet, clipSize, ammoPerItem);
        }
    }
}
