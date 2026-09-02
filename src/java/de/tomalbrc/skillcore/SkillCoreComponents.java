package de.tomalbrc.skillcore;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.tomalbrc.skillcore.api.Skill;
import de.tomalbrc.skillcore.api.SkillTrigger;
import de.tomalbrc.skillcore.io.Json;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillCoreComponents {
    public static final DataComponentType<Skillset> SKILLS = new DataComponentType.Builder<Skillset>().persistent(Skillset.CODEC).build();
    public static final DataComponentType<Ammo> AMMO = new DataComponentType.Builder<Ammo>().persistent(Ammo.CODEC).build();
    public static final DataComponentType<Integer> MAGAZINE = new DataComponentType.Builder<Integer>().persistent(Codec.INT).build();

    public static void register() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath("skillcore", "skills"), SKILLS);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath("skillcore", "ammo"), AMMO);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath("skillcore", "magazine"), MAGAZINE);
        PolymerComponent.registerDataComponent(SKILLS, AMMO, MAGAZINE);
    }

    public static class Skillset {
        final List<String> skillsStrings;
        final List<Skill> skills;
        final Map<SkillTrigger, List<Skill>> map;

        public Skillset(List<String> skills) {
            this.skillsStrings = skills;
            this.skills = new ArrayList<>();
            this.map = new HashMap<>();
            for (String skill : skills) {
                var parsed = Json.GSON.fromJson(new JsonPrimitive(skill), Skill.class);
                if (parsed != null) {
                    this.skills.add(parsed);
                    this.map.computeIfAbsent(parsed.trigger(), k -> new ArrayList<>()).add(parsed);
                }
            }
        }

        public @Nullable List<Skill> skillsForTrigger(SkillTrigger skillTrigger) {
            return map.get(skillTrigger);
        }

        public List<String> skills() {
            return skillsStrings;
        }

        public static final Codec<Skillset> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.STRING.listOf().fieldOf("skills").forGetter(Skillset::skills)).apply(instance, Skillset::new));
    }

    public static class Ammo {
        String bullet;
        int clipSize;
        int ammoPerItem;

        public Ammo(String bullet, int clipSize, int ammoPerItem) {
            this.bullet = bullet;
            this.clipSize = clipSize;
            this.ammoPerItem = ammoPerItem;
        }

        public String bullet() {
            return this.bullet;
        }

        public int clipSize() {
            return clipSize;
        }

        public int ammoPerItem() {
            return this.ammoPerItem;
        }

        public static final Codec<Ammo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        Codec.STRING.fieldOf("bullet").forGetter(Ammo:: bullet),
                Codec.INT.fieldOf("clip_size").forGetter(Ammo::clipSize),
                Codec.INT.fieldOf("ammo_per_item").forGetter(Ammo::ammoPerItem)
                ).apply(instance, Ammo::new)
        );
    }
}
