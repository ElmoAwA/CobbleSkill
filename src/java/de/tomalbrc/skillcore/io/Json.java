package de.tomalbrc.skillcore.io;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import de.tomalbrc.skillcore.api.SkillHealthCondition;
import de.tomalbrc.skillcore.api.condition.Conditions;
import de.tomalbrc.skillcore.api.mechanic.Mechanics;
import de.tomalbrc.skillcore.api.target.Targeters;
import de.tomalbrc.skillcore.data.EquipmentEntry;
import de.tomalbrc.skillcore.impl.MetaSkillRef;
import de.tomalbrc.skillcore.impl.variable.Resolvable;
import de.tomalbrc.skillcore.util.RangedValue;
import de.tomalbrc.skillcore.util.TextUtil;
import de.tomalbrc.skillcore.util.WeightedSkillList;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class Json {
    public static final Codec<ResourceLocation> LOWERCASE_RESOURCE_LOCATION_CODEC =
            Codec.STRING
                    .comapFlatMap(
                            str -> {
                                try {
                                    return DataResult.success(ResourceLocation.parse(str.toLowerCase()));
                                } catch (Exception e) {
                                    return DataResult.error(() -> "Invalid id: " + str);
                                }
                            },
                            ResourceLocation::toString
                    )
                    .stable();

    public static final Gson GSON = new GsonBuilder()
            //.setStrictness(Strictness.LENIENT)
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeHierarchyAdapter(BlockState.class, new BlockStateDeserializer())
            .registerTypeHierarchyAdapter(Vector3f.class, new SimpleCodecDeserializer<>(ExtraCodecs.VECTOR3F))
            .registerTypeHierarchyAdapter(Quaternionf.class, new QuaternionfDeserializer())
            .registerTypeAdapter(ResourceLocation.class, new SimpleCodecDeserializer<>(LOWERCASE_RESOURCE_LOCATION_CODEC))
            .registerTypeHierarchyAdapter(Component.class, new ComponentDeserializer())
            .registerTypeHierarchyAdapter(Display.BillboardConstraints.class, new SimpleCodecDeserializer<>(Display.BillboardConstraints.CODEC))
            .registerTypeHierarchyAdapter(EquipmentSlot.class, new SimpleCodecDeserializer<>(EquipmentSlot.CODEC))
            .registerTypeHierarchyAdapter(BlockModelType.class, new LowercaseEnumDeserializer<>(BlockModelType.class))
            .registerTypeHierarchyAdapter(Difficulty.class, new SimpleCodecDeserializer<>(Difficulty.CODEC))
            .registerTypeHierarchyAdapter(MobCategory.class, new SimpleCodecDeserializer<>(MobCategory.CODEC))
            .registerTypeHierarchyAdapter(ItemDisplayContext.class, new SimpleCodecDeserializer<>(ItemDisplayContext.CODEC))
            .registerTypeHierarchyAdapter(PushReaction.class, new LowercaseEnumDeserializer<>(PushReaction.class))
            .registerTypeHierarchyAdapter(WeatheringCopper.WeatherState.class, new SimpleCodecDeserializer<>(WeatheringCopper.WeatherState.CODEC))
            .registerTypeHierarchyAdapter(Block.class, new RegistryDeserializer<>(BuiltInRegistries.BLOCK))
            .registerTypeHierarchyAdapter(Item.class, new RegistryDeserializer<>(BuiltInRegistries.ITEM))
            .registerTypeHierarchyAdapter(SoundEvent.class, new RegistryDeserializer<>(BuiltInRegistries.SOUND_EVENT))
            .registerTypeHierarchyAdapter(PolymerBlockModel.class, new PolymerBlockModelDeserializer())
            // skills
            .registerTypeHierarchyAdapter(RangedValue.class, new RangedValue.Deserializer())
            .registerTypeHierarchyAdapter(WeightedSkillList.class, new WeightedSkillList.Deserializer())
            .registerTypeAdapterFactory(Conditions.TYPE_ADAPTER_FACTORY)
            .registerTypeAdapterFactory(Mechanics.TYPE_ADAPTER_FACTORY)
            .registerTypeAdapterFactory(Targeters.TYPE_ADAPTER_FACTORY)
            .registerTypeAdapterFactory(new SkillAdapterFactory())
            .registerTypeAdapterFactory(new EquipmentEntry.AdapterFactory())
            .registerTypeAdapterFactory(new StringListAdapterFactory())
            .registerTypeHierarchyAdapter(SkillHealthCondition.class, new SkillHealthCondition.Deserializer())
            .registerTypeHierarchyAdapter(Resolvable.class, new Resolvable.Deserializer<>())
            .registerTypeHierarchyAdapter(MetaSkillRef.class, new MetaSkillRef.Deserializer())
            .create();

    public static class BlockStateDeserializer implements JsonDeserializer<BlockState> {
        @Override
        public BlockState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String name = json.getAsString().toLowerCase();

            BlockStateParser.BlockResult parsed;
            try {
                parsed = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), name, false);
            } catch (CommandSyntaxException e) {
                throw new JsonParseException("Invalid BlockState value: " + name);
            }

            return parsed.blockState();
        }
    }

    public static class LowercaseEnumDeserializer<T extends Enum<T>> implements JsonDeserializer<T> {

        private final Class<T> enumClass;

        public LowercaseEnumDeserializer(Class<T> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String value = json.getAsString().toLowerCase();
            for (T constant : enumClass.getEnumConstants()) {
                if (constant.name().equalsIgnoreCase(value)) {
                    return constant;
                }
            }

            throw new JsonParseException("Invalid " + enumClass.getSimpleName() + " value: " + value);
        }
    }

    public static class PolymerBlockModelDeserializer implements JsonDeserializer<PolymerBlockModel> {
        @Override
        public PolymerBlockModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                JsonPrimitive primitive = json.getAsJsonPrimitive();
                if (primitive.isString()) {
                    return PolymerBlockModel.of(ResourceLocation.tryParse(primitive.getAsString()));
                }
            } else if (json.isJsonObject()) {
                JsonObject object = json.getAsJsonObject();
                ResourceLocation model = ResourceLocation.parse(object.get("model").getAsString());
                int x = object.has("x") ? object.get("x").getAsInt() : 0;
                int y = object.has("y") ? object.get("y").getAsInt() : 0;
                boolean uvLock = object.has("uvLock") && object.get("uvLock").getAsBoolean();
                int weight = object.has("weight") ? object.get("weight").getAsInt() : 1;
                return PolymerBlockModel.of(model, x, y, uvLock, weight);
            }

            throw new JsonParseException("Invalid PolymerBlockModel value: " + json);
        }
    }

    public static class QuaternionfDeserializer implements JsonDeserializer<Quaternionf> {
        @Override
        public Quaternionf deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            if (jsonArray.size() < 3) {
                throw new JsonParseException("Array size should be at least 3 for euler angle deserialization.");
            }

            float x = jsonArray.get(0).getAsFloat();
            float y = jsonArray.get(1).getAsFloat();
            float z = jsonArray.get(2).getAsFloat();

            return new Quaternionf().rotateXYZ(x * Mth.DEG_TO_RAD, y * Mth.DEG_TO_RAD, z * Mth.DEG_TO_RAD);
        }
    }

    private record RegistryDeserializer<T>(Registry<T> registry) implements JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            return this.registry.get(ResourceLocation.parse(element.getAsString()));
        }
    }

    private record ComponentDeserializer() implements JsonDeserializer<Component> {
        @Override
        public Component deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            return TextUtil.formatText(element.getAsString());
        }
    }
}
