package de.tomalbrc.skillcore.data;

import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.registry.ItemRegistry;
import de.tomalbrc.skillcore.util.BukkitIdConverter;
import de.tomalbrc.skillcore.util.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.*;

public record CustomItemData(
        @SerializedName("identifier") String id,
        @SerializedName(value = "name", alternate = {"display", "n", "d"}) String name,
        int data,
        Integer model,
        @SerializedName(value = "amount", alternate = "a") int amount,
        @SerializedName(value = "lore", alternate = "l") String lore,
        @SerializedName(value = "enchantments", alternate = {"enchants", "ench", "e"}) List<String> enchantments,
        @SerializedName(value = "potioneffects", alternate = {"peffects", "potion", "pe"}) List<String> potioneffects,
        @SerializedName(value = "color", alternate = {"c", "potioncolor", "pcolor", "pc"}) String color,
        @SerializedName(value = "skullowner", alternate = "skullOwner") String skullowner,
        @SerializedName(value = "skulltexture", alternate = "skullTexture") String skulltexture
 ) {
    public ItemStack asItemStack() {
        var custom = ItemRegistry.get(id);
        if (custom != null) {
            return custom.asItemStack();
        }

        int count = Math.max(amount, 1);
        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(id.toLowerCase(Locale.ROOT))), count);

        if (lore != null) {
            var loreLines = Arrays.stream(lore.split("\n")).map(TextUtil::formatText).toList();
            stack.set(DataComponents.LORE, new ItemLore(loreLines, loreLines));
        }

        if (name != null) {
            stack.set(DataComponents.ITEM_NAME, TextUtil.formatText(name));
        }

        if (model != null) {
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(model));
        }

        if (color != null) {
            var f = ChatFormatting.getByName(color);
            if (f != null) {
                if (f.getColor() != null) stack.set(DataComponents.DYED_COLOR, new DyedItemColor(f.getColor(), true));
            }
        }

        if (skullowner != null || skulltexture != null) {
            var properties = new PropertyMap();
            if (skulltexture != null)
                properties.put("textures", new Property("textures", skulltexture));

            stack.set(DataComponents.PROFILE, new ResolvableProfile(
                    Optional.ofNullable(skullowner),
                    Optional.empty(),
                    properties
            ));
        }

        if (enchantments != null) {
            var enchants = new ItemEnchantments.Mutable(stack.getEnchantments());

            for (String enchantment : enchantments) {
                enchantment = enchantment.trim();

                var e = enchantment.split(" ");
                var realEid = BukkitIdConverter.enchantment(e[0]).orElse(ResourceLocation.parse(e[0].toLowerCase(Locale.ROOT)));
                var enchant = SkillCore.SERVER.registryAccess().asGetterLookup().get(Registries.ENCHANTMENT, ResourceKey.create(Registries.ENCHANTMENT, realEid));
                enchant.ifPresent(x -> enchants.set(x, e.length > 1 ? Integer.parseInt(e[1]) : 1));
            }

            stack.set(DataComponents.ENCHANTMENTS, enchants.toImmutable());
        }

        if (potioneffects != null) {
            List<MobEffectInstance> mobEffects = new ArrayList<>();
            for (String potioneffect : potioneffects) {
                mobEffects.add(new MobEffectInstance(
                        BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(potioneffect)).orElseThrow()
                ));
            }
            stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), mobEffects));
        }

        return stack;
    }
}
