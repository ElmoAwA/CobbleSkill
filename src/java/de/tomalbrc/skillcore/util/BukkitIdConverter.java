package de.tomalbrc.skillcore.util;

import de.tomalbrc.skillcore.SkillCore;
import de.tomalbrc.skillcore.io.Json;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

import java.io.InputStreamReader;
import java.util.*;

public class BukkitIdConverter {
    // JSON Data from MockBukkit
    private static final Map<String, ResourceLocation> ENCHANTMENT = new HashMap<>();
    private static final Map<String, ResourceLocation> POTION = new HashMap<>();
    private static final Map<String, ResourceLocation> MOB_EFFECT = new HashMap<>();
    private static final Map<String, ResourceLocation> ENTITY_TYPE = new HashMap<>();
    public static final Map<String, String> PARTICLE = new Object2ObjectArrayMap<>(
            Map.ofEntries(
                    Map.entry("mobSpellAmbient", "minecraft:ambient_entity_effect"),
                    Map.entry("villager_angry", "minecraft:angry_villager"),
                    Map.entry("angryVillager", "minecraft:angry_villager"),
                    Map.entry("blockdust", "minecraft:block"),
                    Map.entry("blockcrack", "minecraft:block"),
                    Map.entry("damageIndicator", "minecraft:damage_indicator"),
                    Map.entry("dragonbreath", "minecraft:dragon_breath"),
                    Map.entry("dripLava", "minecraft:dripping_lava"),
                    Map.entry("dripWater", "minecraft:dripping_water"),
                    Map.entry("snowball", "minecraft:item{item:snowball}"),
                    Map.entry("reddust", "minecraft:dust{color:[1,0,0],scale:1}"),
                    Map.entry("redstone", "minecraft:dust{color:[1,0,0],scale:1}"),
                    Map.entry("spell", "minecraft:effect"),
                    Map.entry("mobappearance", "minecraft:elder_guardian"),
                    Map.entry("enchantmenttable", "minecraft:enchant"),
                    Map.entry("magicCrit", "minecraft:enchanted_hit"),
                    Map.entry("crit_magic", "minecraft:enchanted_hit"),
                    Map.entry("endRod", "minecraft:end_rod"),
                    Map.entry("mobSpell", "minecraft:entity_effect"),
                    Map.entry("largeexplosion", "minecraft:explosion"),
                    Map.entry("largeexplode", "minecraft:explosion"),
                    Map.entry("hugeexplosion", "minecraft:explosion_emitter"),
                    Map.entry("fallingdust", "minecraft:falling_dust"),
                    Map.entry("fireworksSpark", "minecraft:firework"),
                    Map.entry("wake", "minecraft:fishing"),
                    Map.entry("happyVillager", "minecraft:happy_villager"),
                    Map.entry("instantSpell", "minecraft:instant_effect"),
                    Map.entry("iconcrack", "minecraft:item"),
                    Map.entry("slime", "minecraft:item_slime"),
                    Map.entry("snowballpoof", "minecraft:item_snowball"),
                    Map.entry("largesmoke", "minecraft:large_smoke"),
                    Map.entry("smoke_normal", "minecraft:smoke"),
                    Map.entry("smoke_large", "minecraft:large_smoke"),
                    Map.entry("townaura", "minecraft:mycelium"),
                    Map.entry("explode", "minecraft:poof"),
                    Map.entry("snowshovel", "minecraft:poof"),
                    Map.entry("droplet", "minecraft:rain"),
                    Map.entry("sweepAttack", "minecraft:sweep_attack"),
                    Map.entry("totem", "minecraft:totem_of_undying"),
                    Map.entry("suspended", "minecraft:underwater"),
                    Map.entry("witchMagic", "minecraft:witch")
            )
    );

    public static void load() {
        ENCHANTMENT.putAll(load("enchantment"));
        POTION.putAll(load("potion"));
        load("particle_type").forEach((k,v) -> {
            PARTICLE.put(k, v.toString());
        });
        MOB_EFFECT.putAll(load("mob_effect"));
        ENTITY_TYPE.putAll(load("entity_type"));
    }

    public static Map<String, ResourceLocation> load(String name) {
        var map = new HashMap<String, ResourceLocation>();

        var eJson = SkillCore.class.getResourceAsStream("/" + name + ".json");
        if (eJson != null) {
            BukkitRecordList e = Json.GSON.fromJson(new InputStreamReader(eJson), BukkitRecordList.class);
            for (BukkitNameKeyRecord value : e.values) {
                map.put(value.name, value.key);
            }
        }
        return map;
    }

    public static Optional<ResourceLocation> enchantment(String name) {
        return Optional.ofNullable(ENCHANTMENT.get(name.toLowerCase(Locale.ROOT)));
    }

    public static Optional<String> particle(String name) {
        return Optional.ofNullable(PARTICLE.get(name));
    }

    public static Optional<ResourceLocation> potion(String name) {
        return Optional.ofNullable(POTION.get(name));
    }

    public static Optional<EquipmentSlot> slot(String slotName) {
        if (slotName == null) {
            return Optional.empty();
        }

        return switch (slotName.toUpperCase()) {
            case "HEAD" -> Optional.of(EquipmentSlot.HEAD);
            case "CHEST" -> Optional.of(EquipmentSlot.CHEST);
            case "LEGS" -> Optional.of(EquipmentSlot.LEGS);
            case "FEET" -> Optional.of(EquipmentSlot.FEET);
            case "HAND", "MAINHAND" -> Optional.of(EquipmentSlot.MAINHAND);
            case "OFFHAND", "OFF_HAND" -> Optional.of(EquipmentSlot.OFFHAND);
            default -> Optional.empty();
        };
    }

    public static Optional<ResourceLocation> mobEffect(String potionType) {
        return Optional.ofNullable(MOB_EFFECT.get(potionType));
    }

    public static Optional<ResourceLocation> entityType(String t) {
        return Optional.ofNullable(ENTITY_TYPE.get(t));
    }

    private record BukkitRecordList(
            List<BukkitNameKeyRecord> values
    ) { }

    public record BukkitNameKeyRecord(
            ResourceLocation key,
            String name
    ) { }
}
