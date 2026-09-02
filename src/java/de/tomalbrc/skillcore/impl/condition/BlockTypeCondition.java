package de.tomalbrc.skillcore.impl.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.tomalbrc.skillcore.api.execution.SkillTree;
import de.tomalbrc.skillcore.api.target.Target;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;
import java.util.regex.Pattern;

public class BlockTypeCondition extends AbstractCondition {
    @SerializedName(value = "types", alternate = {"type", "t", "material", "mat", "m", "block", "b"})
    Set<String> types = ImmutableSet.of("DIRT");

    @Override
    public boolean test(SkillTree tree, Target target) {
        Level world = tree.level();
        BlockPos pos = target.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        ResourceLocation blockName = BuiltInRegistries.BLOCK.getKey(block);

        for (String type : types) {
            type = type.trim().toLowerCase();

            if (type.startsWith("#")) {
                TagKey<Block> tag = TagKey.create(Registries.BLOCK, ResourceLocation.parse(type.substring(1)));
                if (block.builtInRegistryHolder().is(tag)) return true;
            }

            else if (type.contains("*")) {
                String regex = type.replace("*", ".*");
                if (Pattern.matches(regex, blockName.getPath())) return true;
            }

            else if (type.contains("[")) {
                try {
                    return state == BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), type, false).blockState();
                } catch (CommandSyntaxException e) {
                    return false;
                }
            } else {
                if (blockName.getPath().equalsIgnoreCase(type))
                    return true;
            }
        }

        return false;
    }
}